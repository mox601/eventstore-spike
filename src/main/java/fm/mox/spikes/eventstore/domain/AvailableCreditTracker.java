package fm.mox.spikes.eventstore.domain;

import lombok.Value;

import java.util.Map;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com.
 */
@Value
public class AvailableCreditTracker {
    //see https://youtu.be/trU3AlBdbsk?t=30m44s
    private CustomerId customerId;
    private int creditLimit;
    private Map<OrderId, Integer> creditReservations;

    //2. publish availablecreditchanged

    //3. send commands from customer to order using a kafka command bus
    //an AvailableCreditTracker mirrors the Customer
    //OrderCreationSaga
    //a. EVENT OrderCreated
    //b. COMMAND ReserveCredit
    //c. EVENT CreditReserved
    //d. COMMAND ApproveOrder
    //e. COMMAND UpdateAvailableCredit towards Customer
}
