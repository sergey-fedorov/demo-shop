package com.demo.shop.tests.integration;

import com.demo.shop.model.Customer;
import com.demo.shop.model.Order;
import com.demo.shop.model.OrderItem;
import com.demo.shop.model.Product;
import com.demo.shop.service.*;
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
import java.time.format.DateTimeFormatter;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
public class DbIntegrationTests {

    @Autowired
    CustomerService customerService;
    @Autowired
    OrderService orderService;
    @Autowired
    ProductService productService;
    @Autowired
    OrderItemService orderItemService;

    @Container
    static MySQLContainer mySQLContainer = new MySQLContainer<>("mysql:8.1.0");

    @DynamicPropertySource
    static void dynamicConfiguration(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);

    }

    Customer customer = Customer.builder()
            .email("testname@email.com")
            .address("City")
            .fullName("Test Name")
            .build();
    Product product = Product.builder()
            .name("Test product")
            .price(5.0)
            .pictureUrl("product.png")
            .build();
    Order order = Order.builder()
            .status("TEST")
            .build();

    @Test
    public void shouldSaveAndFetchCustomer() {
        Long customerId = customerService.create(customer).getId();
        Assertions.assertEquals(
                customer,
                customerService.get(customerId)
        );
    }

    @Test
    public void shouldSaveAndFetchProduct() {
        Long productId = productService.create(product).getId();
        Assertions.assertEquals(
                product,
                productService.getProduct(productId)
        );
    }

    @Test
    public void shouldSaveAndFetchOrder() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("uuuu-MM-dd HH:mm:ss");
        Long customerId = customerService.create(customer.setEmail("testname2@email.com")).getId();
        order.setCustomer(Customer.builder().id(customerId).build());

        Long orderId = orderService.create(order).getId();
        LocalDateTime now = LocalDateTime.now();

        Order orderRecord = orderService.get(orderId);

        Assertions.assertEquals(orderId, orderRecord.getId());
        Assertions.assertEquals("TEST", orderRecord.getStatus());
        Assertions.assertEquals(dtf.format(now), orderRecord.getDateCreated().format(dtf));
        Assertions.assertNull(orderRecord.getTransactionId());
        Assertions.assertEquals(customerId, orderRecord.getCustomerId());
    }

    @Test
    public void shouldSaveAndFetchOrderItem() {
        Long customerId = customerService.create(customer.setEmail("testname3@email.com")).getId();
        int quantity = 2;
        Long productId = productService.create(product).getId();
        order.setCustomer(Customer.builder().id(customerId).build());
        Long orderId = orderService.create(order).getId();
        OrderItem orderItem = new OrderItem(
                Order.builder().id(orderId).build(),
                Product.builder().id(productId).build(),
                quantity);

        OrderItem orderItemRecord = orderItemService.create(orderItem);

        Assertions.assertEquals(quantity, orderItemRecord.getQuantity());
        Assertions.assertEquals(productId, orderItemRecord.getProductId());
        Assertions.assertEquals(productId, orderItemRecord.getProductId());
    }

    @Test
    public void shouldNotSaveOrder_CustomerIdFKConstraint() {
        Long customerId = 999L;
        order.setCustomer(Customer.builder().id(customerId).build());

        Exception exception = Assertions.assertThrows(
                DataIntegrityViolationException.class, () -> orderService.create(order));
        Assertions.assertTrue(exception.getMessage().contains("FOREIGN KEY (`customer_id`) REFERENCES `customers` (`id`)"));
    }

    @Test
    public void shouldNotSaveOrderItem_ProductIdFKConstraint() {
        Long customerId = customerService.create(customer.setEmail("testname4@email.com")).getId();
        int quantity = 2;
        Long productId = 999L;
        order.setCustomer(Customer.builder().id(customerId).build());
        Long orderId = orderService.create(order).getId();
        OrderItem orderItem = new OrderItem(
                Order.builder().id(orderId).build(),
                Product.builder().id(productId).build(),
                quantity);

        Exception exception = Assertions.assertThrows(
                DataIntegrityViolationException.class, () -> orderItemService.create(orderItem));
        Assertions.assertTrue(exception.getMessage().contains("FOREIGN KEY (`product_id`) REFERENCES `product` (`id`)"));
    }


    @Test
    public void shouldNotSaveCustomer_DuplicateEntry() {
        customerService.create(customer.setEmail("testname5@email.com"));

        Customer customerDuplicate = Customer.builder()
                .email("testname5@email.com")
                .address("City")
                .fullName("Test Name")
                .build();

        Exception exception = Assertions.assertThrows(
                DataIntegrityViolationException.class, () -> customerService.create(customerDuplicate));
        Assertions.assertTrue(exception.getMessage().contains("Duplicate entry"));
    }


}
