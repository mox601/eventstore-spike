package fm.mox.spikes.eventstore.domain;

import fm.mox.spikes.eventstore.domain.events.OrderAccepted;
import fm.mox.spikes.eventstore.domain.events.OrderCreated;
import fm.mox.spikes.eventstore.domain.events.OrderRejected;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com.
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class Order extends EventSourcedRootEntity {

    private OrderId id;

    private CustomerId customerId;

    private State state;

    private int total;

    private Order() {
        super();
    }

    public Order(OrderId id, CustomerId customerId, State state, int total) {
        this();
        super.apply(new OrderCreated(id, customerId, state, total));
    }

    public Order(List<DomainEvent> anEventStream, int aStreamVersion) {
        super(anEventStream, aStreamVersion);
    }

    public void accept() {
        if (this.state.equals(State.PENDING)) {
            super.apply(new OrderAccepted(this.id, this.customerId));
        }
    }

    public void reject() {
        if (this.state.equals(State.PENDING)) {
            super.apply(new OrderRejected(this.id, this.customerId, new Date()));
        }
    }

    void when(OrderCreated anEvent) {
        this.id = anEvent.getId();
        this.customerId = anEvent.getCustomerId();
        this.state = anEvent.getState();
        this.total = anEvent.getTotal();
    }

    void when(OrderAccepted anEvent) {
        this.state = State.ACCEPTED;
    }

    void when(OrderRejected anEvent) {
        this.state = State.REJECTED;
    }

    public enum State {
        PENDING,
        ACCEPTED,
        REJECTED
    }
}
