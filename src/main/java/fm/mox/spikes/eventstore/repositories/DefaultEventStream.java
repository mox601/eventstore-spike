package fm.mox.spikes.eventstore.repositories;

import fm.mox.spikes.eventstore.domain.DomainEvent;
import lombok.Value;

import java.util.List;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com.
 */
@Value
public class DefaultEventStream implements EventStream {
    private final List<DomainEvent> events;
    private final int version;
    @Override
    public List<DomainEvent> events() {
        return this.events;
    }

    @Override
    public int version() {
        return this.version;
    }

}
