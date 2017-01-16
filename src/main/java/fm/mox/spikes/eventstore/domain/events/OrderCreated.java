package fm.mox.spikes.eventstore.domain.events;

import fm.mox.spikes.eventstore.domain.CustomerId;
import fm.mox.spikes.eventstore.domain.DomainEvent;
import fm.mox.spikes.eventstore.domain.Order;
import fm.mox.spikes.eventstore.domain.OrderId;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Tolerate;

import java.util.Date;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com.
 */
@Value
public class OrderCreated implements DomainEvent {

    @NonNull
    private final OrderId id;
    @NonNull
    private final CustomerId customerId;
    @NonNull
    private final Order.State state;
    @NonNull
    private final int total;
    @NonNull
    private final Date occurredOn;

    @Tolerate
    private OrderCreated() {
        this(new OrderId("-"), new CustomerId("-"), Order.State.PENDING, -1, new Date());
    }

    @Tolerate
    public OrderCreated(OrderId id, CustomerId customerId, Order.State state, int total) {
        this(id, customerId, state, total, new Date());
    }

    @Override
    public int eventVersion() {
        return 0;
    }

    @Override
    public Date occurredOn() {
        return occurredOn;
    }
}
