package fm.mox.spikes.eventstore.service.orders;

import com.github.msemys.esjc.PersistentSubscription;
import com.github.msemys.esjc.PersistentSubscriptionCreateResult;
import com.github.msemys.esjc.PersistentSubscriptionListener;
import com.github.msemys.esjc.ResolvedEvent;
import com.github.msemys.esjc.subscription.PersistentSubscriptionNakEventAction;
import fm.mox.spikes.eventstore.domain.DomainEvent;
import fm.mox.spikes.eventstore.repositories.impl.EventMapper;
import fm.mox.spikes.eventstore.repositories.impl.StreamNames;
import fm.mox.spikes.eventstore.service.EventStoreSubscriber;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeoutException;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com.
 */
@Slf4j
public class EventStoreOrdersEventSubscriber implements PersistentSubscriptionListener {

    private static final String EVENT_STORE_ORDERS_EVENT_LISTENER = "ordersEventListeners";

    private final EventStoreSubscriber eventStoreSubscriber;
    private final EventMapper eventMapper;
    private final OrdersEventHandler ordersEventHandler;

    private PersistentSubscription persistentSubscription;

    public EventStoreOrdersEventSubscriber(EventStoreSubscriber eventStoreSubscriber, EventMapper eventMapper, OrdersEventHandler ordersEventHandler) {
        this.eventStoreSubscriber = eventStoreSubscriber;
        this.eventMapper = eventMapper;
        this.ordersEventHandler = ordersEventHandler;
    }

    //creates and subscribes to a persistent subscription at startup

    public void createSubscriptionAndSubscribe() {
        try {
            final PersistentSubscriptionCreateResult persistentSubscriptionCreateResult =
                    this.eventStoreSubscriber.createPersistentSubscription(
                            StreamNames.CUSTOMERS_CATEGORY,
                            EVENT_STORE_ORDERS_EVENT_LISTENER);
            log.info(persistentSubscriptionCreateResult.status.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        //subscribeToPersistentAndGet this stream as group
        this.persistentSubscription = this.eventStoreSubscriber.subscribeToPersistentAndGet(StreamNames.CUSTOMERS_CATEGORY,
                EVENT_STORE_ORDERS_EVENT_LISTENER, this);
    }

    public void close() throws TimeoutException {
        this.persistentSubscription.close();
    }

    @Override
    public void onEvent(PersistentSubscription subscription, ResolvedEvent event) {

        DomainEvent domainEvent = this.eventMapper.deserialize(event);

        try {
            this.ordersEventHandler.handle(domainEvent);
            subscription.acknowledge(event);
            log.info("handled " + domainEvent.toString());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            //depending on type of error perform an action
            subscription.fail(event, PersistentSubscriptionNakEventAction.Retry, e.getMessage());
        }
    }
}
