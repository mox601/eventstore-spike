package fm.mox.spikes.eventstore.domain.events;

import fm.mox.spikes.eventstore.domain.CustomerId;
import fm.mox.spikes.eventstore.domain.DomainEvent;
import fm.mox.spikes.eventstore.domain.OrderId;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Tolerate;

import java.util.Date;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com.
 */
@Value
public class OrderRejected implements DomainEvent {

    @NonNull
    private final OrderId orderId;
    @NonNull
    private final CustomerId customerId;
    private final Date occurredOn;

    @Tolerate
    public OrderRejected() {
        this(new OrderId("-"), new CustomerId("-"), new Date(0));
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
