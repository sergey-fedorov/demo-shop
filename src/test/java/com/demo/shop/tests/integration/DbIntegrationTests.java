package com.demo.shop.tests.integration;

import com.demo.shop.model.*;
import com.demo.shop.repository.CustomerRepository;
import com.demo.shop.repository.OrderItemRepository;
import com.demo.shop.repository.OrderRepository;
import com.demo.shop.repository.ProductRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.time.LocalDateTime;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class DbIntegrationTests {

    @Autowired
    CustomerRepository customerRepository;
    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    OrderItemRepository orderItemRepository;

    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer<>("mysql:8.1.0");

    @DynamicPropertySource
    static void dynamicConfiguration(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
    }


    @Test
    public void shouldSaveAndFetchCustomer() {
        Customer customer = Customer.builder().email("testname@email.com").address("City").fullName("Test Name").build();
        Long customerId = customerRepository.save(customer).getId();
        Assertions.assertEquals(
                customer,
                customerRepository.findById(customerId).orElseThrow()
        );
    }

    @Test
    public void shouldSaveAndFetchProduct() {
        Product product = Product.builder().name("Test product").price(5.0).pictureUrl("product.png").build();
        Long productId = productRepository.save(product).getId();
        Assertions.assertEquals(
                product,
                productRepository.findById(productId).orElseThrow()
        );
    }

    @Test
    public void shouldSaveAndFetchOrder() {
        LocalDateTime now = LocalDateTime.now();
        Customer customer = Customer.builder().email("testname2@email.com").address("City").fullName("Test Name").build();
        Customer customerRecord = customerRepository.save(customer);
        Order order = Order.builder().dateCreated(now).customer(customerRecord).status("NEW").build();

        Long orderId = orderRepository.save(order).getId();
        Order orderRecord = orderRepository.findById(orderId).orElseThrow();

        Assertions.assertEquals(order.getStatus(), orderRecord.getStatus());
        Assertions.assertEquals(now, orderRecord.getDateCreated());
        Assertions.assertNull(orderRecord.getTransactionId());
        Assertions.assertEquals(customerRecord.getId(), orderRecord.getCustomerId());
    }

    @Test
    public void shouldSaveAndFetchOrderItem() {
        Customer customer = Customer.builder().email("testname3@email.com").address("City").fullName("Test Name").build();
        Customer customerRecord = customerRepository.save(customer);
        Product product = Product.builder().name("Test product").price(5.0).pictureUrl("product.png").build();
        Product productRecord = productRepository.save(product);
        Order order = Order.builder().dateCreated(LocalDateTime.now()).customer(customerRecord).status("NEW").build();
        Order orderRecord = orderRepository.save(order);

        OrderItem orderItem = new OrderItem(orderRecord, productRecord, 2);
        orderItemRepository.save(orderItem);
        OrderItem orderItemRecord = orderItemRepository.findById(new OrderItemPK(orderRecord, productRecord)).orElseThrow();

        Assertions.assertEquals(orderItem.getQuantity(), orderItemRecord.getQuantity());
        Assertions.assertEquals(orderItem.getOrder().getId(), orderItemRecord.getOrder().getId());
        Assertions.assertEquals(orderItem.getProduct().getId(), orderItemRecord.getProduct().getId());
    }

    @Test
    public void shouldNotSaveOrder_MissingCustomer_CustomerIdFKConstraint() {
        Long customerId = 999L;
        Customer customer = Customer.builder().id(customerId).build();
        Order order = Order.builder().dateCreated(LocalDateTime.now()).customer(customer).status("NEW").build();

        Exception exception = Assertions.assertThrows(
                DataIntegrityViolationException.class, () -> orderRepository.save(order));
        Assertions.assertTrue(exception.getMessage().contains("FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`)"));
    }

    @Test
    public void shouldNotSaveOrderItem_MissingProduct_ProductIdFKConstraint() {
        Long productId = 999L;
        Customer customer = Customer.builder().email("testname4@email.com").address("City").fullName("Test Name").build();
        Customer customerRecord = customerRepository.save(customer);
        Product product = Product.builder().id(productId).build();
        Order order = Order.builder().dateCreated(LocalDateTime.now()).customer(customerRecord).status("NEW").build();
        Order orderRecord = orderRepository.save(order);

        OrderItem orderItem = new OrderItem(orderRecord, product, 2);

        Exception exception = Assertions.assertThrows(
                DataIntegrityViolationException.class, () -> orderItemRepository.save(orderItem));
        Assertions.assertTrue(exception.getMessage().contains("FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)"));
    }


    @Test
    public void shouldNotSaveCustomer_ExistingCustomer_DuplicateEntry() {
        Customer customerOne = Customer.builder().email("testname5@email.com").address("City").fullName("Test Name").build();
        Customer customerTwo = Customer.builder().email("testname5@email.com").address("City").fullName("Test Name").build();
        customerRepository.save(customerOne);

        Exception exception = Assertions.assertThrows(
                DataIntegrityViolationException.class, () -> customerRepository.save(customerTwo));
        Assertions.assertTrue(exception.getMessage().contains("Duplicate entry"));
    }


}
