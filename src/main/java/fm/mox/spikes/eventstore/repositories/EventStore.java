package fm.mox.spikes.eventstore.repositories;

import fm.mox.spikes.eventstore.domain.DomainEvent;

import java.util.List;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com.
 */
public interface EventStore {

    void appendWith(EventStreamId aStartingIdentity, List<DomainEvent> anEvents);

    void close();

//    List<DispatchableDomainEvent> eventsSince(long aLastReceivedEvent);

    EventStream eventStreamSince(EventStreamId anIdentity);

    EventStream fullEventStreamFor(EventStreamId anIdentity);

    void purge(); // mainly used for testing

//    void registerEventNotifiable(EventNotifiable anEventNotifiable);
}
