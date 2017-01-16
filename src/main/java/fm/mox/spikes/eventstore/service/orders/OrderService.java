package fm.mox.spikes.eventstore.service.orders;

import fm.mox.spikes.eventstore.domain.CustomerId;
import fm.mox.spikes.eventstore.domain.Order;
import fm.mox.spikes.eventstore.domain.OrderId;
import fm.mox.spikes.eventstore.domain.OrderRepository;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com.
 */
@Slf4j
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void createOrder(CreateOrderCommand aCommand) {
        // create order
        OrderId id = this.orderRepository.nextIdentity();
        CustomerId customerId = aCommand.getCustomerId();
        int total = aCommand.getTotal();
        Order order = new Order(id, customerId, Order.State.PENDING, total);

        // store events of aggregate
        this.orderRepository.save(order);
    }

    @Value
    public static class CreateOrderCommand {
        private final CustomerId customerId;
        private final int total;
    }
}
