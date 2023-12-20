package com.demo.shop.tests.contract.provider;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.StateChangeAction;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import com.demo.shop.model.Customer;
import com.demo.shop.model.Order;
import com.demo.shop.model.OrderItem;
import com.demo.shop.model.Product;
import com.demo.shop.repository.CustomerRepository;
import com.demo.shop.repository.OrderItemRepository;
import com.demo.shop.repository.OrderRepository;
import com.demo.shop.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Provider("orders_provider")
@PactFolder("src/test/resources/pacts")
@Testcontainers
public class ProviderOrdersServiceTest {

   /* Using running service with mysql testcontainer */

    @LocalServerPort
    private int port;

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;

    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer<>("mysql:8.1.0");

    @DynamicPropertySource
    static void dynamicConfiguration(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);

    }

    @TestTemplate
    @ExtendWith(PactVerificationInvocationContextProvider.class)
    void pactVerificationTestTemplate(PactVerificationContext context) {
        context.verifyInteraction();
    }

    Product product = Product.builder().id(1L).name("product").pictureUrl("url.png").price(2.0).build();
    Customer customer = Customer.builder().id(1L).email("any@email.com").fullName("any name").address("any address").build();

    @BeforeEach
    void setup(PactVerificationContext context) {
        context.setTarget(new HttpTestTarget("localhost", port));
        productRepository.save(product);
        customerRepository.save(customer);
    }

    @State(value = "orders with PAYMENT_SUCCEEDED and other statuses exist", action = StateChangeAction.SETUP)
    public void ordersExist() {
        Order orderPaid = Order.builder().dateCreated(LocalDateTime.now()).customer(customer).status("PAYMENT_SUCCEEDED").build();
        Order orderNew = Order.builder().dateCreated(LocalDateTime.now()).customer(customer).status("NEW").build();
        List<OrderItem> orderItemsPaid = List.of(new OrderItem(orderPaid, product, 1));
        List<OrderItem> orderItemsNew = List.of(new OrderItem(orderNew, product, 1));

        orderRepository.save(orderPaid);
        orderItemsPaid.forEach(item -> orderItemRepository.save(item));
        orderRepository.save(orderNew);
        orderItemsNew.forEach(item -> orderItemRepository.save(item));
    }

    @State(value = "no orders with status PAYMENT_SUCCEEDED exist", action = StateChangeAction.SETUP)
    public void paidOrderDoesNotExist() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }

    @State(value = "order with status PAYMENT_SUCCEEDED exists", action = StateChangeAction.SETUP)
    public Map<String, Long> paidOrderExist() {
        Order order = Order.builder().dateCreated(LocalDateTime.now()).customer(customer).status("PAYMENT_SUCCEEDED").build();
        List<OrderItem> orderItems = List.of(new OrderItem(order, product, 1));

        Long orderId = orderRepository.save(order).getId();
        orderItems.forEach(item -> orderItemRepository.save(item));

        return Map.of("orderId", orderId);
    }

    @State(value = "order with id 999 does not exist", action = StateChangeAction.SETUP)
    public void orderDoesNotExist() {
        orderItemRepository.deleteAll();
        orderRepository.deleteAll();
    }

}