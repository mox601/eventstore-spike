package fm.mox.spikes.eventstore.domain;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com.
 */
public interface CustomerRepository {

    Customer customerOfId(CustomerId customerId);

    CustomerId nextIdentity();

    void save(Customer customer);
}
