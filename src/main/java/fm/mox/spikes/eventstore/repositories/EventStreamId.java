package fm.mox.spikes.eventstore.repositories;

import lombok.Value;
import lombok.experimental.Tolerate;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com.
 */
@Value
public final class EventStreamId {

    private static final int INITIAL_VERSION = 0;

    private final String streamName;
    private final int streamVersion;

    @Tolerate
    public EventStreamId(String aStreamName) {
        this(aStreamName, INITIAL_VERSION);
    }

    @Tolerate
    public EventStreamId(String aStreamNameSegment1, String aStreamNameSegment2) {
        this(aStreamNameSegment1, aStreamNameSegment2, INITIAL_VERSION);
    }

    @Tolerate
    public EventStreamId(String aStreamNameSegment1, String aStreamNameSegment2, int aStreamVersion) {
        this(aStreamNameSegment1 + ":" + aStreamNameSegment2, aStreamVersion);
    }

    public String streamName() {
        return this.streamName;
    }

    public int streamVersion() {
        return this.streamVersion;
    }

}
