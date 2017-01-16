package fm.mox.spikes.eventstore;

import com.github.msemys.esjc.EventData;
import com.github.msemys.esjc.EventStore;
import com.github.msemys.esjc.EventStoreBuilder;
import com.github.msemys.esjc.ExpectedVersion;
import com.github.msemys.esjc.RecordedEvent;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static java.util.Arrays.asList;

// TODO take domain model from https://www.youtube.com/watch?v=trU3AlBdbsk
@Slf4j
public class EventStoreIT {

    private EventStore eventstore;

    @BeforeClass
    public void creatingAClientInstance() throws Exception {
        eventstore = EventStoreBuilder.newBuilder()
                .singleNodeAddress("127.0.0.1", 1113)
                .userCredentials("admin", "changeit")
                .build();
    }

    @AfterClass
    public void tearDown() throws Exception {
        this.eventstore.disconnect();
    }

    @Test(enabled = false)
    public void testAsync() throws Exception {
        // handles result asynchronously
        this.eventstore.appendToStream("foo", ExpectedVersion.any(), asList(
                EventData.newBuilder().type("bar").jsonData("{ a : 1 }").build(),
                EventData.newBuilder().type("baz").jsonData("{ b : 2 }").build())
        ).thenAccept(r -> log.info(r.logPosition + ""));
    }

    @Test(enabled = false)
    public void testSync() throws Exception {
        // handles result synchronously
        this.eventstore.appendToStream("foo", ExpectedVersion.any(), asList(
                EventData.newBuilder().type("bar").jsonData("{ a : 1 }").build(),
                EventData.newBuilder().type("baz").jsonData("{ b : 2 }").build())
        ).thenAccept(r -> log.info(r.logPosition + "")).get();
    }

    @Test(enabled = false)
    public void testSub() throws Exception {
        this.eventstore.readStreamEventsForward("$ce-user", 0, 100, true)
                .thenAccept(e ->
                        e.events.forEach(i -> {
                            RecordedEvent recordedEvent = i.event;
                            String data = new String(recordedEvent.data);
                            String metadata = new String(recordedEvent.metadata);
                            log.info(recordedEvent.eventNumber + " id: '" + recordedEvent.eventId + "'; " + "type: '"
                                    + recordedEvent.eventType + "'; " + "data: '" + data + "; " + "metadata: '" + metadata + "'");
                        })).get();
    }
}
