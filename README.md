# demo-shop
That project has been created to showcase the test development on different levels from unit to end-to-end, building a CI/CD pipeline from scratch, and integrating tests to it for a microservices-based application.

## Application under test
AUT has been created using the Spring Boot framework and MySQL database.
It is a backend app with the simple functionality of an abstract e-commerce solution that allows a customer to place an order with a set of products.

### External integrations
It communicates with three backend services: a third-party email validator and two very basic services for order payment and delivery that are located in the current codebase to reduce deployment and support expenses.

<p align="center">
  <img src="https://github.com/sergey-fedorov/demo-shop/assets/11277217/9b49db51-7534-497b-99d5-cc38d52c9ba3"/>
</p>

### Build and deploy
Tools: Maven, Docker, GitHub Actions, Docker Hub, AWS EC2

Application is built with Maven, dockerized and deployed to EC2 instance.

CI/CD pipeline configuration before the tests integration:

<p align="center">
  <img src="https://github.com/sergey-fedorov/demo-shop/assets/11277217/d1b6b5a8-1608-43f0-9a18-d0aae00e1dd8"/>
</p>

## Test levels
According to the classic Test Pyramid the main portion of tests are created on unit level and the smallest amount on E2E level.
Test Pyramid for microservices-based application will contain the following levels (from bottom to top):
* Unit
* Integration
  * DB
  * External services
  * Contract
* Component
* End-to-end

### Unit tests
Mocking: yes <br/>
Tools: MockMvc

### Integration tests
Application acts as consumer for payment and email-validator services, and as provider for the delivery service.

<p align="center">
  <img src="https://github.com/sergey-fedorov/demo-shop/assets/11277217/cf799450-33a6-4233-932f-28f9206844a0"/>
</p>

#### DB tests
Focus on the CRUD operations between application data layer and DB.

Mocking: yes, replace the real DB <br/>
Tools: Testcontainers

#### External services tests
Focus on the communication interface with other services.

Mocking: yes, all other services <br/>
Tools: WireMock

#### Contract tests
Focus on the communication interface between consumer and provider, ignoring the internal business logic implementation.

Mocking: consumer - mock provider service, provider - mock data layer <br/>
Tools: Pact, Testcontainers <br/>

Side note: Contract testing is an enhanced version of a regular integration testing.
Consumer side tests are executing against a mocked provider service and generate contract file.
On provider side tests the data layer should be mocked as well as external services.
One of the challenges is a communication between consumer and provider to share the updated contract file.

### Component tests
Mocking: <br/>
Tools:

### End-to-end tests
Mocking: no <br/>
Tools: RestAssured

## Tests integration into CI/CD pipeline


<p align="center">
  <img src="https://github.com/sergey-fedorov/demo-shop/assets/11277217/c6d458e6-59ca-4cee-acc2-3c9987b1d0b5"/>
</p>
