# Order Service

A Java Spring Boot microservice for creating orders and publishing order events to Kafka using a clean architecture pattern with use cases, interfaces, and contracts.

## Features

- **Order Creation** – REST API endpoint to create new orders
- **Event-Driven Architecture** – Publishes order events to Kafka for downstream processing
- **Clean Architecture** – Separation of concerns with use cases, interfaces, and contracts
- **In-Memory Database** – H2 database for development and testing
- **API Documentation** – SpringDoc OpenAPI with Swagger UI

## Project Structure

```
src/main/java/com/example/orderservice/
├── OrderserviceApplication.java          # Spring Boot main class
├── app/
│   ├── core/                              # Business logic
│   │   ├── origin/                        # Shared base classes
│   │   │   ├── entities/                  # AbstractService, AbstractUsecase
│   │   │   ├── schemas/                   # ServiceOutput, ServiceDependency, User
│   │   │   └── spring/                    # ControllerServiceExecutor
│   │   └── orders/                        # Orders domain
│   │       ├── entities/                  # Order, OrderItem, OrderStatus
│   │       └── features/
│   │           └── createOrder/
│   │               ├── contracts/         # CONTRACT_HELPER_CreateOrder_V0
│   │               ├── interfaces/        # INTERFACE_HELPER_CreateOrder
│   │               ├── schemas/           # INPUT_CreateOrder, OUTPUT_CreateOrder
│   │               ├── services/          # SERVICE_CreateOrder
│   │               ├── usecases/          # USECASE_CreateOrder
│   │               └── spring/            # BEAN_CreateOrder
│   └── infra/                             # Infrastructure layer
│       ├── events/
│       │   ├── contracts/                 # KafkaService
│       │   ├── entities/                  # Event
│       │   └── interfaces/                # EventService
│       └── logger/                        # Logging utilities
└── framework/                             # Framework entrypoints
    └── entrypoints/
        └── api/
            └── orders/
                └── controllers/           # OrdersController
```

## Tech Stack

- **Java 25**
- **Spring Boot 4.0.1**
- **Spring Data JPA** – ORM and data access
- **Spring Kafka** – Kafka producer integration
- **H2 Database** – In-memory database (development)
- **Lombok** – Boilerplate reduction
- **MapStruct** – Object mapping
- **SpringDoc OpenAPI** – API documentation
- **spring-dotenv** – Environment configuration

## Prerequisites

- Java 25+
- Maven 3.8+
- Apache Kafka (running on `localhost:9092`)

## Installation

```bash
# Clone and navigate to the project
cd order-service

# Install dependencies
./mvnw clean install
```

## Configuration

Configuration is managed via `src/main/resources/application.yaml`:

```yaml
spring:
  application:
    name: orderservice
  datasource:
    url: jdbc:h2:mem:orderdb
    driver-class-name: org.h2.Driver
  h2:
    console:
      enabled: true
      path: /h2-console
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true
  kafka:
    bootstrap-servers: localhost:9092
```

### Configuration Options

| Property                         | Description                | Default               |
| -------------------------------- | -------------------------- | --------------------- |
| `spring.kafka.bootstrap-servers` | Kafka broker address       | `localhost:9092`      |
| `spring.datasource.url`          | Database connection URL    | `jdbc:h2:mem:orderdb` |
| `spring.h2.console.enabled`      | Enable H2 console          | `true`                |
| `spring.jpa.hibernate.ddl-auto`  | Schema generation strategy | `create-drop`         |

## Running the Service

### Development

```bash
./mvnw spring-boot:run
```

### Production

```bash
./mvnw clean package
java -jar target/orderservice-0.0.1-SNAPSHOT.jar
```

## API Endpoints

### Create Order

**POST** `/api/v0/orders/create`

Creates a new order and publishes an `OrderCreated` event to Kafka.

**Request Body:**

```json
{
  "customerId": "123",
  "items": [
    {
      "productId": "prod-001",
      "quantity": 2,
      "price": 29.99
    }
  ],
  "totalAmount": 59.98
}
```

**Response:**

```json
{
  "status": "SUCCESS",
  "data": {
    "orderId": "uuid-here",
    "status": "CREATED"
  },
  "errorMessage": null
}
```

### API Documentation

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

### H2 Console

- **URL**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:orderdb`
- **Username**: `sa`
- **Password**: _(empty)_

## Running Tests

```bash
./mvnw test
```

## Architecture

This service follows a **Clean Architecture** pattern:

| Layer          | Description                                                       |
| -------------- | ----------------------------------------------------------------- |
| **Entities**   | Domain models (e.g., `Order`, `OrderItem`, `Event`)               |
| **Use Cases**  | Business logic (e.g., `USECASE_CreateOrder`)                      |
| **Interfaces** | Abstract contracts (e.g., `INTERFACE_HELPER_CreateOrder`)         |
| **Contracts**  | Concrete implementations (e.g., `CONTRACT_HELPER_CreateOrder_V0`) |
| **Services**   | Orchestrators that wire use cases with helpers                    |
| **Spring**     | Spring beans for dependency injection (e.g., `BEAN_CreateOrder`)  |

## Event Flow

```
┌─────────────────┐     ┌────────────────────┐     ┌─────────────────────┐
│  OrdersController│────▶│  SERVICE_CreateOrder│────▶│  USECASE_CreateOrder │
│ POST /create    │     │  (orchestrator)    │     │  (business logic)   │
└─────────────────┘     └────────────────────┘     └─────────────────────┘
                                                            │
                                                            ▼
                        ┌─────────────────────────────────────────────────┐
                        │  CONTRACT_HELPER_CreateOrder_V0                 │
                        │  └─▶ KafkaService.publishEvent(OrderCreated)    │
                        └─────────────────────────────────────────────────┘
                                                            │
                                                            ▼
                                                ┌─────────────────────┐
                                                │  Kafka Topic        │
                                                │  (order-events)     │
                                                └─────────────────────┘
```

## Naming Conventions

| Type      | Pattern                                        | Example                          |
| --------- | ---------------------------------------------- | -------------------------------- |
| Use Case  | `USECASE_<FeatureName>`                        | `USECASE_CreateOrder`            |
| Interface | `INTERFACE_HELPER_<FeatureName>`               | `INTERFACE_HELPER_CreateOrder`   |
| Contract  | `CONTRACT_HELPER_<FeatureName>_V<N>`           | `CONTRACT_HELPER_CreateOrder_V0` |
| Schema    | `INPUT_<FeatureName>` / `OUTPUT_<FeatureName>` | `INPUT_CreateOrder`              |
| Service   | `SERVICE_<FeatureName>`                        | `SERVICE_CreateOrder`            |
| Bean      | `BEAN_<FeatureName>`                           | `BEAN_CreateOrder`               |

## Related Services

- **order-processing-service** – Python service that consumes order events from Kafka and processes them

## License

MIT
