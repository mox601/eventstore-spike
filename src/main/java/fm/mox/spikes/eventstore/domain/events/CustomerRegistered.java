package fm.mox.spikes.eventstore.domain.events;

import fm.mox.spikes.eventstore.domain.CustomerId;
import fm.mox.spikes.eventstore.domain.DomainEvent;
import fm.mox.spikes.eventstore.domain.OrderId;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Tolerate;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com.
 */
@Value
public class CustomerRegistered implements DomainEvent {

    @NonNull
    private final CustomerId customerId;
    @NonNull
    private final int creditLimit;

    @NonNull
    private final Map<OrderId, Integer> creditReservations;

    @NonNull
    private final Date occurredOn;

    @Tolerate
    public CustomerRegistered() {
        this(new CustomerId("-"), -1, new HashMap<>(0), new Date());
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
