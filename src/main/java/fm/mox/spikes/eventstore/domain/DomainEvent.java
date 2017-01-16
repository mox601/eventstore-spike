package fm.mox.spikes.eventstore.domain;

import java.util.Date;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com.
 */
public interface DomainEvent {

    int eventVersion();

    Date occurredOn();
}
