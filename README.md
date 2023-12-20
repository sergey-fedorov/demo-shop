# demo-shop

That project has been created to showcase the test development on different levels from unit to end-to-end, building a CI/CD pipeline from scratch, and integrating tests to it for a microservices-based application.

## Application under test

AUT has been created using the Spring Boot framework and MySQL database.
It is a backend app with the simple functionality of an abstract e-commerce solution that allows a customer to place an order with a set of products.
It communicates with three other backend services: a third-party email validator and two very basic services for order payment and delivery that are located in the current codebase to reduce deployment and support expenses.

<p align="center">
  <img src="https://github.com/sergey-fedorov/demo-shop/assets/11277217/9b49db51-7534-497b-99d5-cc38d52c9ba3"/>
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
Mock: yes <br/>
Tools: MockMvc

### Integration tests

<p align="center">
  <img src="https://github.com/sergey-fedorov/demo-shop/assets/11277217/cf799450-33a6-4233-932f-28f9206844a0"/>
</p>

#### DB tests
Mock: yes, replace the real DB <br/>
Tools: Testcontainers

#### External services tests
Mock: yes <br/>
Tools: WireMock

#### Contract tests
Mock: consumer - yes, provider - repository layer <br/>
Tools: Pact, Testcontainers

### Component tests
Mock: <br/>
Tools:

### End-to-end tests
Mock: no <br/>
Tools: RestAssured, GSON

## CI/CD
