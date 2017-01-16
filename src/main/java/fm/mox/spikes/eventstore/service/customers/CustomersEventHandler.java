package fm.mox.spikes.eventstore.service.customers;

import fm.mox.spikes.eventstore.domain.CustomerId;
import fm.mox.spikes.eventstore.domain.OrderId;
import fm.mox.spikes.eventstore.domain.events.OrderCreated;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com.
 */
public class CustomersEventHandler {

    private final CustomerService customerService;

    public CustomersEventHandler(CustomerService customerService) {
        this.customerService = customerService;
    }

    public void handle(OrderCreated orderCreated) {

        CustomerId customerId = orderCreated.getCustomerId();
        OrderId orderId = orderCreated.getId();
        int total = orderCreated.getTotal();
        CustomerService.ReserveCredit reserveCredit = new CustomerService.ReserveCredit(customerId, orderId, total);

        customerService.reserveCredit(reserveCredit);
    }
}
