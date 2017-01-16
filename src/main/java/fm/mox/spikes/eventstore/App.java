package fm.mox.spikes.eventstore;

import com.github.msemys.esjc.EventStoreBuilder;
import com.github.msemys.esjc.Settings;
import com.github.msemys.esjc.node.single.SingleNodeSettings;
import fm.mox.spikes.eventstore.domain.Customer;
import fm.mox.spikes.eventstore.domain.CustomerRepository;
import fm.mox.spikes.eventstore.domain.OrderRepository;
import fm.mox.spikes.eventstore.repositories.EventStore;
import fm.mox.spikes.eventstore.repositories.impl.ESEventStore;
import fm.mox.spikes.eventstore.repositories.impl.EventMapper;
import fm.mox.spikes.eventstore.repositories.impl.EventStoreCustomerRepository;
import fm.mox.spikes.eventstore.repositories.impl.EventStoreOrderRepository;
import fm.mox.spikes.eventstore.service.EventStoreSubscriber;
import fm.mox.spikes.eventstore.service.customers.CustomerService;
import fm.mox.spikes.eventstore.service.customers.CustomersEventHandler;
import fm.mox.spikes.eventstore.service.customers.EventStoreCustomersEventSubscriber;
import fm.mox.spikes.eventstore.service.orders.EventStoreOrdersEventSubscriber;
import fm.mox.spikes.eventstore.service.orders.OrderService;
import fm.mox.spikes.eventstore.service.orders.OrdersEventHandler;
import lombok.extern.slf4j.Slf4j;
import org.codehaus.jackson.map.ObjectMapper;

import java.util.concurrent.TimeoutException;

@Slf4j
public class App {
    public static void main(String[] args) throws TimeoutException {
        log.info("Hello");

        //persistence
        Settings.Builder builder = Settings.newBuilder();
        Settings build = builder
                .nodeSettings(SingleNodeSettings.newBuilder()
                        .address("127.0.0.1", 1113)
                        .build())
                .build();

        com.github.msemys.esjc.EventStore eventStorem = EventStoreBuilder.newBuilder(build)
                .userCredentials("admin", "changeit")
                .build();


        EventMapper eventMapper = new EventMapper(new ObjectMapper());
        EventStore eventStore = new ESEventStore(eventStorem, eventMapper);

        OrderRepository orderRepository = new EventStoreOrderRepository(eventStore);
        CustomerRepository customerRepository = new EventStoreCustomerRepository(eventStore);

        //services
        CustomerService customerService = new CustomerService(customerRepository);

        //handlers
        OrdersEventHandler ordersEventHandler = new OrdersEventHandler(orderRepository);
        CustomersEventHandler customersEventHandler = new CustomersEventHandler(customerService);

        EventStoreSubscriber eventStoreSubscriber = new EventStoreSubscriber(eventStorem);

        EventStoreOrdersEventSubscriber eventStoreOrdersEventListener =
                new EventStoreOrdersEventSubscriber(eventStoreSubscriber, eventMapper, ordersEventHandler);
        EventStoreCustomersEventSubscriber eventStoreCustomersEventListener =
                new EventStoreCustomersEventSubscriber(eventStoreSubscriber, eventMapper, customersEventHandler);

        eventStoreCustomersEventListener.createSubscriptionAndSubscribe();
        eventStoreOrdersEventListener.createSubscriptionAndSubscribe();

        //services
        OrderService orderService = new OrderService(orderRepository);

        //
        Customer customer = new Customer(customerRepository.nextIdentity(), 100);

        customerRepository.save(customer);

        log.info(customer + "");

        orderService.createOrder(new OrderService.CreateOrderCommand(customer.getId(), 10));

        try {
            Thread.sleep(10_000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        eventStoreOrdersEventListener.close();
        eventStoreCustomersEventListener.close();

        eventStore.close();
    }
}
