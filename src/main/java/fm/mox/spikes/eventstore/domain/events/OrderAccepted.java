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
public class OrderAccepted implements DomainEvent {

    @NonNull
    private final OrderId id;
    @NonNull
    private final CustomerId customerId;
    private final Date occurredOn;

    @Tolerate
    public OrderAccepted() {
        this(new OrderId("-"), new CustomerId("-"), new Date(0L));
    }
    @Tolerate
    public OrderAccepted(OrderId id, CustomerId customerId) {
        this(id, customerId, new Date());
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
