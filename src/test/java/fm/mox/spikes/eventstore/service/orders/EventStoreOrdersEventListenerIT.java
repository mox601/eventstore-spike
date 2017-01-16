package fm.mox.spikes.eventstore.service.orders;

import com.github.msemys.esjc.EventStore;
import com.github.msemys.esjc.EventStoreBuilder;
import fm.mox.spikes.eventstore.domain.CustomerId;
import fm.mox.spikes.eventstore.domain.CustomerRepository;
import fm.mox.spikes.eventstore.domain.OrderRepository;
import fm.mox.spikes.eventstore.repositories.impl.ESEventStore;
import fm.mox.spikes.eventstore.repositories.impl.EventMapper;
import fm.mox.spikes.eventstore.repositories.impl.EventStoreCustomerRepository;
import fm.mox.spikes.eventstore.repositories.impl.EventStoreOrderRepository;
import fm.mox.spikes.eventstore.service.EventStoreSubscriber;
import fm.mox.spikes.eventstore.service.customers.CustomerService;
import fm.mox.spikes.eventstore.service.customers.CustomersEventHandler;
import fm.mox.spikes.eventstore.service.customers.EventStoreCustomersEventSubscriber;
import org.codehaus.jackson.map.ObjectMapper;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Created by matteo (dot) moci (at) gmail (dot) com.
 */
public class EventStoreOrdersEventListenerIT {

    private EventStore eventStore;

    @BeforeClass
    public void setUp() throws Exception {
        this.eventStore = EventStoreBuilder.newBuilder()
                .singleNodeAddress("127.0.0.1", 1113)
                .userCredentials("admin", "changeit")
                .build();
    }

    @AfterClass
    public void tearDown() throws Exception {
        this.eventStore.disconnect();
    }

    @Test(enabled = true)
    public void testName() throws Exception {

        //infrastructure
        ObjectMapper objectMapper = new ObjectMapper();
        EventMapper eventMapper = new EventMapper(objectMapper);
        fm.mox.spikes.eventstore.repositories.EventStore eventstore =
                new ESEventStore(eventStore, eventMapper);
        EventStoreSubscriber eventStoreSubscriber = new EventStoreSubscriber(eventStore);

        //repositories
        OrderRepository orderRepository = new EventStoreOrderRepository(eventstore);
        CustomerRepository customerRepository =
                new EventStoreCustomerRepository(eventstore);

        //services
        CustomerService customerService = new CustomerService(customerRepository);
        OrderService orderService = new OrderService(orderRepository);

        //handlers
        OrdersEventHandler ordersEventHandler = new OrdersEventHandler(orderRepository);
        CustomersEventHandler customersEventHandler = new CustomersEventHandler(customerService);

        //eventstore subscribers
        EventStoreOrdersEventSubscriber eventStoreOrdersEventSubscriber =
                new EventStoreOrdersEventSubscriber(eventStoreSubscriber, eventMapper, ordersEventHandler);
        eventStoreOrdersEventSubscriber.createSubscriptionAndSubscribe();

        EventStoreCustomersEventSubscriber eventStoreCustomersEventSubscriber =
                new EventStoreCustomersEventSubscriber(eventStoreSubscriber, eventMapper, customersEventHandler);
        eventStoreCustomersEventSubscriber.createSubscriptionAndSubscribe();


        //actions

        //register customer
        CustomerId registeredCustomerId = customerService.registerCustomer(100);

        Thread.sleep(1_000);

        //customer buys 10
        OrderService.CreateOrderCommand customerBuysTen =
                new OrderService.CreateOrderCommand(registeredCustomerId, 10);
        orderService.createOrder(customerBuysTen);

        Thread.sleep(1_000);

        //customer buys 99
        OrderService.CreateOrderCommand customerBuysNinetyNine =
                new OrderService.CreateOrderCommand(registeredCustomerId, 99);
        orderService.createOrder(customerBuysNinetyNine);

        Thread.sleep(10_000);

        //shutdown subscribers
        eventStoreOrdersEventSubscriber.close();
        eventStoreCustomersEventSubscriber.close();

    }
}