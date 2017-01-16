package fm.mox.spikes.eventstore.service.orders;

import fm.mox.spikes.eventstore.domain.OrderId;
import fm.mox.spikes.eventstore.domain.events.CreditReserved;
import fm.mox.spikes.eventstore.domain.events.OrderCreated;
import fm.mox.spikes.eventstore.service.customers.CustomerService;
import lombok.Value;
import lombok.experimental.Tolerate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com.
 */
public class OrderProcessRouter {

    //TODO how to handle saga's state?
    //http://tech.just-eat.com/2015/05/26/process-managers/
    private final OrderCreationRepository orderCreationRepository;
    private final CustomerService customerService;

    public OrderProcessRouter(OrderCreationRepository orderCreationRepository, CustomerService customerService) {
        this.orderCreationRepository = orderCreationRepository;
        this.customerService = customerService;
    }

    public void handle(OrderCreated orderCreated) {
        //and to make sure you dont start it more than once if you have at-least-once delivery of events,
        // you can use the aggregate ID and version of the event of a certain type for that
        OrderCreationSaga orderCreationSaga = this.orderCreationRepository.loadBy(orderCreated.getId());

        if (orderCreationSaga == null) {
            orderCreationSaga = new OrderCreationSaga();
        }

        OrderCreationSaga mutatedSaga = orderCreationSaga.when(orderCreated);

        for (Object command : mutatedSaga.commandsToSend) {
            //fire commands
            this.customerService.handleCommand(command);
        }

        this.orderCreationRepository.save(orderCreationSaga);
    }

    public void handle(CreditReserved creditReserved) {

    }

    //model states before and after sending commands, like this: "waiting order creation"
    public enum OrderProcessState {
        NOT_STARTED,
        ORDER_CREATED,
        WAITING_FOR_CREDIT_RESERVED,
        CREDIT_RESERVED,
        ORDER_APPROVED,
        CUSTOMER_CREDIT_UPDATED
    }

    public interface OrderCreationRepository {
        OrderCreationSaga loadBy(OrderId orderId);
        void save(OrderCreationSaga orderCreationSaga);
    }

    @Value
    public static class OrderCreationSaga {

        private final OrderProcessState orderProcessState;

        private final List<Object> commandsToSend;

        @Tolerate
        public OrderCreationSaga() {
            this(OrderProcessState.NOT_STARTED);
        }

        @Tolerate
        public OrderCreationSaga(OrderProcessState orderProcessState) {
            this(orderProcessState, new ArrayList<>());
        }

        public OrderCreationSaga when(OrderCreated orderCreated) {
            OrderCreationSaga mutatedSaga = null;
            switch (orderProcessState) {
                case NOT_STARTED:
                    CustomerService.ReserveCredit reserveCredit = new CustomerService.ReserveCredit(
                            orderCreated.getCustomerId(),
                            orderCreated.getId(),
                            orderCreated.getTotal());
                    List<Object> commandsToSend = Arrays.asList(reserveCredit);
                    //change state to waiting for credit reserved
                    mutatedSaga = new OrderCreationSaga(OrderProcessState.WAITING_FOR_CREDIT_RESERVED, commandsToSend);
                    break;
                default:
                    throw new IllegalStateException("not allowed state " + orderProcessState);
            }
            return mutatedSaga;
        }
    }
}
