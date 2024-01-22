# demo-shop
That project has been created to showcase the test development on different levels from unit to end-to-end, building a CI/CD pipeline from scratch, and integrating tests to it for a microservices-based application.

## Table of contents
- [Application under test](#application-under-test)</br>
  - [External integrations](#external-integrations)
  - [Build and deploy](#build-and-deploy)
- [Test levels](#test-levels)</br>
  - [Unit tests](#unit-tests)
  - [Integration tests](#integration-tests)
    - [DB tests](#db-tests)
    - [External services tests](#external-services-tests)
    - [Contract tests](#contract-tests)
  - [Component tests](#component-tests)
  - [End-to-end tests](#end-to-end-tests)
- [Tests integration into CI/CD pipeline](#tests-integration-into-cicd-pipeline)</br>
- [State-transition testing](#state-transition-testing)</br>
- [Todo](#todo)

## Application under test
AUT has been created using the Spring Boot framework and MySQL database.
It is a backend app with the simple functionality of an abstract e-commerce solution that allows a customer to place an order with a set of products.

Swagger specs: http://18.193.88.110:8081/swagger-ui/index.html

### External integrations
It communicates with three backend services: a third-party email validator and two very basic services for order payment and delivery that are located in the current codebase to reduce deployment and support expenses.

<p align="center">
  <img src="https://github.com/sergey-fedorov/demo-shop/assets/11277217/52fdfc82-14aa-4fdb-ac5d-b0a72c6002fd"/>
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

## Tests reporting
Allure reporting tool has been integrated to test automation framework and built into CI/CD pipeline. Report with its history hosted on GitHub Pages: <br/>
https://sergey-fedorov.github.io/demo-shop/

## Tests integration into CI/CD pipeline

<p align="center">
  <img src="https://github.com/sergey-fedorov/demo-shop/assets/11277217/12b76328-1e5b-420f-9614-3a3a5f235914"/>
</p>

## State-transition testing
Popular black-box test design technique to express complex logic and interactions in a compact notation.
This technique is ideal for testing transitions between different order statuses of AUT.

1. Create state-transition diagram that includes all order statuses and indicates application events that trigger status changing.
<p align="center">
  <img  width="450" src="https://github.com/sergey-fedorov/demo-shop/assets/11277217/b9bbdb37-57d8-4f81-b12d-2b21a8377121"/>
</p>

2. Create state-transition table based on diagram where each line is a transitions (represented by an arrow on diagram).
<p align="center">
  <img width="700" src="https://github.com/sergey-fedorov/demo-shop/assets/11277217/24dfae60-ba90-41cc-8af3-29541ba33256"/>
</p>

3. Create test cases based on table and make sure all transitions are covered.
<p align="center">
  <img width="450" src="https://github.com/sergey-fedorov/demo-shop/assets/11277217/e4ffb66f-499c-470c-a8d0-0dfe55a90add"/>
</p>



## Todo

- [ ] README: Add brief description for test level sections
- [ ] APP: Add JaCoCo code coverage library, add it to ci/cd
- [ ] TEST: Add componen tests
- [ ] TEST: Add Pact Broker for contracts sharing
- [ ] TEST: Add more E2E tests
- [ ] README: Describe test automation framework used for E2E tests
- [ ] TEST: Add logging library
- [x] TEST: Add reporting for E2E tests
- [x] APP: Add Swagger Specification
- [x] README: Create state transition diagram for order statuses and tests based on it






