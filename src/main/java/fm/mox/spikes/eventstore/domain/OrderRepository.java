package fm.mox.spikes.eventstore.domain;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com.
 */
public interface OrderRepository {

    Order orderOfId(CustomerId customerId, OrderId orderId);

    OrderId nextIdentity();

    void save(Order order);
}
