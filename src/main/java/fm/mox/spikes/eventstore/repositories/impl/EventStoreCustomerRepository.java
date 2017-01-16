package fm.mox.spikes.eventstore.repositories.impl;

import fm.mox.spikes.eventstore.domain.Customer;
import fm.mox.spikes.eventstore.domain.CustomerId;
import fm.mox.spikes.eventstore.domain.CustomerRepository;
import fm.mox.spikes.eventstore.repositories.EventStore;
import fm.mox.spikes.eventstore.repositories.EventStream;
import fm.mox.spikes.eventstore.repositories.EventStreamId;

import java.util.UUID;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com.
 */
public class EventStoreCustomerRepository implements CustomerRepository {

    private final EventStore eventStore;

    public EventStoreCustomerRepository(EventStore eventStore) {
        this.eventStore = eventStore;
    }

    @Override
    public Customer customerOfId(CustomerId customerId) {
        // snapshots not currently supported; always use version 1

        EventStreamId eventId = new EventStreamId(customerId.getId());

        EventStream eventStream = this.eventStore.eventStreamSince(eventId);

        return new Customer(eventStream.events(), eventStream.version());

    }

    @Override
    public CustomerId nextIdentity() {
        return new CustomerId(StreamNames.CUSTOMER_DASH + UUID.randomUUID().toString().toUpperCase());
    }

    @Override
    public void save(Customer customer) {

        EventStreamId eventId = new EventStreamId(
                customer.getId().getId(),
                customer.mutatedVersion());

        this.eventStore.appendWith(eventId, customer.mutatingEvents());


    }
}
