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
public class CreditReserved implements DomainEvent {

    @NonNull
    private final CustomerId customerId;
    @NonNull
    private final OrderId orderId;
    private final int total;
    @NonNull
    private final Date occurredOn;

    @Tolerate
    private CreditReserved() {
        this(new CustomerId("-"), new OrderId("-"), -1, new Date(0));
    }

    @Override
    public int eventVersion() {
        return 0;
    }

    @Override
    public Date occurredOn() {
        return this.occurredOn;
    }
}
