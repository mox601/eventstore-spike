package fm.mox.spikes.eventstore.repositories.impl;

import com.github.msemys.esjc.EventData;
import com.github.msemys.esjc.RecordedEvent;
import com.github.msemys.esjc.ResolvedEvent;
import fm.mox.spikes.eventstore.domain.DomainEvent;
import fm.mox.spikes.eventstore.domain.events.CreditCheckFailed;
import fm.mox.spikes.eventstore.domain.events.CreditReserved;
import fm.mox.spikes.eventstore.domain.events.CustomerRegistered;
import fm.mox.spikes.eventstore.domain.events.OrderCreated;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com.
 */
public class EventMapper {

    private final ObjectMapper objectMapper;

    public EventMapper(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public DomainEvent deserialize(ResolvedEvent resolvedEvent) {

        final RecordedEvent recordedEvent = resolvedEvent.event;

        final String eventType = recordedEvent.eventType;

        final String json = new String(recordedEvent.data);

        final DomainEvent o;

        if (eventType.equals(simpleNameOf(CustomerRegistered.class))) {
            try {
                o = this.objectMapper.readValue(json, CustomerRegistered.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (eventType.equals(simpleNameOf(OrderCreated.class))) {
            try {
                o = this.objectMapper.readValue(json, OrderCreated.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (eventType.equals(simpleNameOf(CreditReserved.class))) {
            try {
                o = this.objectMapper.readValue(json, CreditReserved.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (eventType.equals(simpleNameOf(CreditCheckFailed.class))) {
            try {
                o = this.objectMapper.readValue(json, CreditCheckFailed.class);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new UnsupportedOperationException("can't deserialize '" + eventType + "'");
        }

        return o;
    }

    public String serialize(DomainEvent domainEvent) {
        String asJson = "";
        try {
            asJson = this.objectMapper.writeValueAsString(domainEvent);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return asJson;
    }

    public List<EventData> serializeList(List<DomainEvent> anEvents) {
        return anEvents.stream().map(domainEvent -> {

            final String simpleName = simpleNameOf(domainEvent.getClass());

            final String asJson = this.serialize(domainEvent);

            return EventData.newBuilder().type(simpleName).jsonData(asJson).build();

        }).collect(Collectors.toList());
    }

    private static String simpleNameOf(final Class<?> aClass) {
        return aClass.getSimpleName();
    }

}
