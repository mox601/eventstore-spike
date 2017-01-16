package fm.mox.spikes.eventstore.domain;

import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Tolerate;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com.
 */
@Value
public class CustomerId {
    @NonNull
    private final String id;
    @Tolerate
    private CustomerId() {
        this("-");
    }
}
