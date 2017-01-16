package fm.mox.spikes.eventstore.repositories.impl;

import fm.mox.spikes.eventstore.domain.CustomerId;
import fm.mox.spikes.eventstore.domain.Order;
import fm.mox.spikes.eventstore.domain.OrderId;
import fm.mox.spikes.eventstore.domain.OrderRepository;
import fm.mox.spikes.eventstore.repositories.EventStore;
import fm.mox.spikes.eventstore.repositories.EventStream;
import fm.mox.spikes.eventstore.repositories.EventStreamId;

import java.util.UUID;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com.
 */
public class EventStoreOrderRepository implements OrderRepository {

    private final EventStore eventStore;

    public EventStoreOrderRepository(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public Order orderOfId(CustomerId customerId, OrderId orderId) {
        // snapshots not currently supported; always use version 1

        EventStreamId eventId = new EventStreamId(orderId.getId());

        EventStream eventStream = this.eventStore.eventStreamSince(eventId);

        return new Order(eventStream.events(), eventStream.version());

    }

    @Override
    public OrderId nextIdentity() {
        return new OrderId(StreamNames.ORDER_DASH + UUID.randomUUID().toString().toUpperCase());
    }

    @Override
    public void save(Order order) {

        EventStreamId eventId = new EventStreamId(
                order.getId().getId(),
                order.mutatedVersion());

        this.eventStore.appendWith(eventId, order.mutatingEvents());

    }
}
