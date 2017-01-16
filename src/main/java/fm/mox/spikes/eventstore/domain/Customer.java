package fm.mox.spikes.eventstore.domain;

import fm.mox.spikes.eventstore.domain.events.CreditCheckFailed;
import fm.mox.spikes.eventstore.domain.events.CreditReserved;
import fm.mox.spikes.eventstore.domain.events.CustomerRegistered;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BinaryOperator;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com.
 */
@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
@Slf4j
public class Customer extends EventSourcedRootEntity {

    private static final int RESERVATIONS_LIMIT = 100;

    private static final BinaryOperator<Integer> SUM = (integer, integer2) -> integer + integer2;

    private CustomerId id;

    private int creditLimit;

    private Map<OrderId, Integer> creditReservations;

    private Customer() {
        super();
    }

    public Customer(CustomerId customerId, int creditLimit) {
        this();
        super.apply(new CustomerRegistered(customerId, creditLimit, new HashMap<>(0), new Date()));
    }

    public Customer(List<DomainEvent> anEventStream, int aStreamVersion) {
        super(anEventStream, aStreamVersion);
    }

    public void reserveCredit(OrderId orderId, int total) {

        //TODO check if can reserve credit
        //total reservations do not exceed amount

        Integer totalReservations = creditReservations.entrySet()
                .stream()
                .map(Map.Entry::getValue)
                .reduce(0, SUM);

        //i still have not reserved credit for same order id

        boolean isExceedingLimit = (totalReservations + total) > RESERVATIONS_LIMIT;

        boolean isSameOrderYetReserved = creditReservations.containsKey(orderId);

        if (isExceedingLimit || isSameOrderYetReserved) {
            super.apply(new CreditCheckFailed(this.id, orderId, new Date()));
        } else {
            super.apply(new CreditReserved(this.id, orderId, total, new Date()));
        }
    }

    void when(CustomerRegistered customerRegistered) {
        this.id = customerRegistered.getCustomerId();
        this.creditLimit = customerRegistered.getCreditLimit();
        this.creditReservations = customerRegistered.getCreditReservations();
    }

    void when(CreditReserved anEvent) {
        creditReservations.put(anEvent.getOrderId(), anEvent.getTotal());
    }

    void when(CreditCheckFailed anEvent) {

    }
}
