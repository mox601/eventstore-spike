package fm.mox.spikes.reactor;

import org.testng.annotations.Test;
import reactor.core.Disposable;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com.
 */
public class ReactorTest {

    @Test
    public void testName() throws Exception {
        final Flux<Long> longFlux = Flux.fromIterable(getSomeLongList())
                .mergeWith(Flux.interval(Duration.ofMillis(100)))
                .doOnNext(t -> System.out.println("observed " + t.toString()))
                .map(d -> d * 2)
                .take(30);
        final Disposable subscribe = longFlux.subscribe(System.out::println);
        subscribe.dispose();
    }

    private static Iterable<Long> getSomeLongList() {
        final int initialCapacity = 10_000;
        final List<Long> longs = new ArrayList<>(initialCapacity);
        for (int i = 0; i < initialCapacity; i++) {
            longs.add((long) i);
        }
        return longs;
    }
}
