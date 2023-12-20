# demo-shop

That project has been created to practice the test development on different levels from unit to end-to-end, building a CI/CD pipeline from scratch, and integrating tests to it for a microservices-based application.

### Application under test

AUT has been created using the Spring Boot framework and MySQL database.
It is a backend app with the simple functionality of an abstract e-commerce solution that allows a customer to place an order with a set of products.
It communicates with three other backend services: a third-party email validator and two very basic services for order payment and delivery that are located in the current codebase to reduce deployment and support expenses.
