package fm.mox.spikes.eventstore.service.customers;

import fm.mox.spikes.eventstore.domain.Customer;
import fm.mox.spikes.eventstore.domain.CustomerId;
import fm.mox.spikes.eventstore.domain.CustomerRepository;
import fm.mox.spikes.eventstore.domain.OrderId;
import lombok.Value;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com.
 */
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerId registerCustomer(int creditLimit) {
        final Customer customer = new Customer(customerRepository.nextIdentity(), creditLimit);
        this.customerRepository.save(customer);
        return customer.getId();
    }

    public void handleCommand(Object command) {
        if (command instanceof ReserveCredit) {
            reserveCredit((ReserveCredit) command);
        }
    }

    public void reserveCredit(ReserveCredit reserveCredit) {
        // load
        Customer customer = customerRepository.customerOfId(reserveCredit.customerId);
        // call method
        if (customer != null) {
            customer.reserveCredit(reserveCredit.id, reserveCredit.total);
            // store raised events
            customerRepository.save(customer);
        }
    }

    @Value
    public static class ReserveCredit {
        private final CustomerId customerId;
        private final OrderId id;
        private final int total;
    }
}
