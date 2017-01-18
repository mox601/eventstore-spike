package fm.mox.spikes.eventstore.repositories.impl;

import com.github.msemys.esjc.ExpectedVersion;
import com.github.msemys.esjc.StreamEventsSlice;
import com.github.msemys.esjc.WriteResult;
import fm.mox.spikes.eventstore.domain.DomainEvent;
import fm.mox.spikes.eventstore.repositories.DefaultEventStream;
import fm.mox.spikes.eventstore.repositories.EventStore;
import fm.mox.spikes.eventstore.repositories.EventStream;
import fm.mox.spikes.eventstore.repositories.EventStreamId;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com.
 */
public class ESEventStore implements EventStore {

    private final com.github.msemys.esjc.EventStore eventStore;
    private final EventMapper eventMapper;

    public ESEventStore(com.github.msemys.esjc.EventStore eventStore, EventMapper eventMapper) {
        this.eventStore = eventStore;
        this.eventMapper = eventMapper;
    }

    @Override
    public void appendWith(EventStreamId aStartingIdentity, List<DomainEvent> anEvents) {

        try {

            ExpectedVersion expectedVersion = (aStartingIdentity.getStreamVersion() - 1) == 0 ?
                    ExpectedVersion.ANY : ExpectedVersion.of(aStartingIdentity.getStreamVersion() - 1);

            WriteResult writeResult = this.eventStore.appendToStream(
                    aStartingIdentity.getStreamName(),
                    expectedVersion,
                    eventMapper.serializeList(anEvents))
                    .get();

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() {
        this.eventStore.disconnect();
    }

    @Override
    public EventStream eventStreamSince(EventStreamId anIdentity) {

        EventStream defaultEventStream = null;

        try {

            String stream = anIdentity.streamName();

            int eventNumberInclusiveFrom = anIdentity.streamVersion();

            int maxCount = 10;

            boolean isEndOfStream = false;

            int lastEventNumber = -1;

            List<DomainEvent> events = new ArrayList<>(maxCount);

            while (!isEndOfStream) {

                CompletableFuture<StreamEventsSlice> streamEventsSliceCompletableFuture =
                        this.eventStore.readStreamEventsForward(stream, eventNumberInclusiveFrom, maxCount,
                                false);

                StreamEventsSlice streamEventsSlice = streamEventsSliceCompletableFuture.get();

                List<DomainEvent> collected = streamEventsSlice.events.stream()
                        .map(eventMapper::deserialize)
                        .collect(Collectors.toList());

                events.addAll(collected);

                lastEventNumber = streamEventsSlice.lastEventNumber;

                eventNumberInclusiveFrom = streamEventsSlice.nextEventNumber;

                isEndOfStream = streamEventsSlice.isEndOfStream;
            }

            defaultEventStream = new DefaultEventStream(events, lastEventNumber);

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }

        return defaultEventStream;

    }

    @Override
    public EventStream fullEventStreamFor(EventStreamId anIdentity) {
        return null;
    }

    @Override
    public void purge() {

    }
}
