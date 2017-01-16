package fm.mox.spikes.eventstore.domain;

import fm.mox.spikes.eventstore.domain.events.OrderAccepted;
import fm.mox.spikes.eventstore.domain.events.OrderCreated;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.List;

import static org.testng.Assert.assertEquals;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com.
 */
public class EventSourcedRootEntityTest {

    @Test
    public void givenOrderWhenCreatingThenMutatedVersionIsOne() throws Exception {
        Order order = new Order(new OrderId("1"), new CustomerId("1"), Order.State.PENDING, 1);
        assertEquals(order.mutatedVersion(), 1);
    }

    @Test
    public void givenOrderWhenCreatingThenMutatedVersionIsNext() throws Exception {
        List<DomainEvent> anEventStream = Arrays.asList(
                new OrderCreated(new OrderId("1"), new CustomerId("1"), Order.State.PENDING, 1),
                new OrderAccepted(new OrderId("1"), new CustomerId("1"))
        );
        int aStreamVersion = 2;
        Order order = new Order(anEventStream, aStreamVersion);
        assertEquals(order.mutatedVersion(), aStreamVersion + 1);
    }
}