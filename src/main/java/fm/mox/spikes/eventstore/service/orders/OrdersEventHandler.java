package fm.mox.spikes.eventstore.service.orders;

import fm.mox.spikes.eventstore.domain.DomainEvent;
import fm.mox.spikes.eventstore.domain.Order;
import fm.mox.spikes.eventstore.domain.OrderRepository;
import fm.mox.spikes.eventstore.domain.events.CreditCheckFailed;
import fm.mox.spikes.eventstore.domain.events.CreditReserved;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com.
 */
@Slf4j
public class OrdersEventHandler {

    private final OrderRepository orderRepository;

    public OrdersEventHandler(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void handle(final DomainEvent domainEvent) {

        if (domainEvent instanceof CreditReserved) {
            handle((CreditReserved) domainEvent);
        } else if (domainEvent instanceof CreditCheckFailed) {
            handle((CreditCheckFailed) domainEvent);
        } else {
            //nop
        }
    }

    public void handle(CreditReserved creditReserved) {
        Order order = this.orderRepository.orderOfId(creditReserved.getCustomerId(), creditReserved.getOrderId());
        order.accept();
        this.orderRepository.save(order);
    }

    public void handle(CreditCheckFailed creditCheckFailed) {
        log.info("rejecting order");
        Order order = this.orderRepository.orderOfId(creditCheckFailed.getCustomerId(), creditCheckFailed.getOrderId());
        order.reject();
        this.orderRepository.save(order);
        log.info("order rejected");
    }
}
