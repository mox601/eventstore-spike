package fm.mox.spikes.eventstore.repositories;

import fm.mox.spikes.eventstore.domain.DomainEvent;

import java.util.List;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com.
 */
public interface EventStream {

    List<DomainEvent> events();

    int version();

}
