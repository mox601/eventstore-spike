package fm.mox.spikes.eventstore.service;

import com.github.msemys.esjc.EventStore;
import com.github.msemys.esjc.PersistentSubscription;
import com.github.msemys.esjc.PersistentSubscriptionCreateResult;
import com.github.msemys.esjc.PersistentSubscriptionListener;
import com.github.msemys.esjc.PersistentSubscriptionSettings;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com.
 */
@Slf4j
public class EventStoreSubscriber {

    private final EventStore eventStore;

    public EventStoreSubscriber(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    public PersistentSubscriptionCreateResult createPersistentSubscription(String stream, String groupName) throws ExecutionException, InterruptedException {

        return this.eventStore.createPersistentSubscription(
                stream,
                groupName,
                PersistentSubscriptionSettings.newBuilder().resolveLinkTos(true).build())
                .get();
    }

    public PersistentSubscription subscribeToPersistentAndGet(String customersCategory,
                                                              String eventStoreOrdersEventListener,
                                                              PersistentSubscriptionListener baseEventStoreListener) {
        CompletableFuture<PersistentSubscription> psCf =
                this.eventStore
                        .subscribeToPersistent(
                                customersCategory,
                                eventStoreOrdersEventListener,
                                baseEventStoreListener);

        PersistentSubscription persistentSubscription;
        try {
            persistentSubscription = psCf.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return persistentSubscription;
    }
}
