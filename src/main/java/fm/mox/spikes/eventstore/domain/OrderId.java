package fm.mox.spikes.eventstore.domain;

import lombok.Value;
import lombok.experimental.Tolerate;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com.
 */
@Value
public class OrderId {
    private final String id;
    @Tolerate
    public OrderId() {
        this("-");
    }
}
