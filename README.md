# Order Service

A Java Spring Boot microservice for creating orders and publishing order events to Kafka using a clean architecture pattern with use cases, interfaces, and contracts.

## Features

- **Order Creation** – REST API endpoint to create new orders with validation
- **Event-Driven Architecture** – Publishes order events to Kafka for downstream processing
- **Clean Architecture** – Separation of concerns with use cases, interfaces, and contracts
- **Input Validation** – Comprehensive validation for customer ID, items, and pricing
- **In-Memory Database** – H2 database for development and testing
- **API Documentation** – SpringDoc OpenAPI with Swagger UI
- **Unit Testing** – JUnit 5 with Mockito for comprehensive test coverage

## Project Structure

```
src/
├── main/java/com/example/orderservice/
│   ├── OrderserviceApplication.java          # Spring Boot main class
│   ├── app/
│   │   ├── core/                              # Business logic
│   │   │   ├── origin/                        # Shared base classes
│   │   │   │   ├── contracts/                 # Base contracts
│   │   │   │   ├── entities/                  # AbstractService, AbstractUsecase
│   │   │   │   ├── exceptions/                # AppException
│   │   │   │   ├── interfaces/                # Base interfaces
│   │   │   │   ├── schemas/                   # ServiceOutput, ServiceStatus
│   │   │   │   └── spring/                    # ControllerServiceExecutor
│   │   │   └── orders/                        # Orders domain
│   │   │       ├── entities/                  # Order, OrderItem, OrderStatus
│   │   │       └── features/
│   │   │           └── createOrder/
│   │   │               ├── contracts/         # CONTRACT_HELPER_CreateOrder_V0
│   │   │               ├── exceptions/        # InvalidOrderException
│   │   │               ├── interfaces/        # INTERFACE_HELPER_CreateOrder
│   │   │               ├── schemas/           # INPUT/OUTPUT_CreateOrder, InputOrderItem
│   │   │               ├── services/          # SERVICE_CreateOrder
│   │   │               ├── usecases/          # USECASE_CreateOrder
│   │   │               └── spring/            # BEAN_CreateOrder
│   │   └── infra/                             # Infrastructure layer
│   │       ├── events/
│   │       │   ├── contracts/                 # KafkaService
│   │       │   ├── entities/                  # Event
│   │       │   └── interfaces/                # EventService
│   │       └── logger/                        # Logging utilities
│   └── framework/                             # Framework entrypoints
│       └── entrypoints/
│           └── api/
│               └── orders/
│                   └── controllers/           # OrdersController
└── test/java/com/example/orderservice/
    ├── OrderserviceApplicationTests.java
    └── app/core/orders/features/createOrder/
        └── usecases/
            └── USECASE_CreateOrderTest.java   # Unit tests for CreateOrder
```

## Tech Stack

- **Java 25**
- **Spring Boot 4.0.1**
- **Spring Data JPA** – ORM and data access
- **Spring Kafka** – Kafka producer integration
- **Spring Validation** – Bean validation
- **H2 Database** – In-memory database (development)
- **Lombok 1.18.42** – Boilerplate reduction
- **MapStruct 1.6.3** – Object mapping
- **SpringDoc OpenAPI 2.8.6** – API documentation
- **spring-dotenv 4.0.0** – Environment configuration
- **JUnit 5 + Mockito** – Testing framework

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

### Environment Variables

Copy `.env.example` to `.env`:

```bash
KAFKA_BROKER_URL="localhost:9092"
KAFKA_TOPIC="order-events"
```

### Application Configuration

Configuration is managed via `src/main/resources/application.yaml`:

| Property                         | Description                | Default               |
| -------------------------------- | -------------------------- | --------------------- |
| `spring.kafka.bootstrap-servers` | Kafka broker address       | `${KAFKA_BROKER_URL}` |
| `spring.kafka.topic`             | Kafka topic for events     | `${KAFKA_TOPIC}`      |
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
  "customerId": "CUST-123",
  "items": [
    {
      "productId": "PROD-001",
      "quantity": 2,
      "unitPrice": 29.99
    }
  ],
  "currency": "USD"
}
```

**Response:**

```json
{
  "status": "SUCCESS",
  "data": {
    "orderId": "uuid-here",
    "status": "CREATED",
    "totalAmount": 59.98,
    "currency": "USD",
    "createdAt": "2026-01-04T15:30:00Z"
  },
  "errorMessage": null
}
```

**Validation Errors:**

| Field      | Validation               | Error Message                          |
| ---------- | ------------------------ | -------------------------------------- |
| customerId | Required, non-blank      | "Customer ID is required"              |
| items      | Required, non-empty list | "Order must contain at least one item" |
| currency   | Required, non-blank      | "Currency is required"                 |
| productId  | Required for each item   | "Product ID is required for all items" |
| quantity   | Must be > 0              | "Quantity must be greater than zero"   |
| unitPrice  | Must be > 0              | "Unit price must be greater than zero" |

### API Documentation

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/v3/api-docs

## Running Tests

```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=USECASE_CreateOrderTest

# Run tests with verbose output
./mvnw test -Dtest=USECASE_CreateOrderTest -q
```

### Test Coverage

The `USECASE_CreateOrderTest` includes **24 test cases** covering:

- **Success Cases** – Valid order creation, helper method calls, order building
- **Customer ID Validation** – null, empty, blank values
- **Items Validation** – null, empty list
- **Currency Validation** – null, empty, blank values
- **Order Item Validation** – product ID, quantity, unit price validation
- **Edge Cases** – No helper calls on validation failure

## Architecture

### Architecture Philosophy

This codebase implements **Vertical Slice Architecture** combined with **Clean Architecture** principles, designed for **feature-driven development** in enterprise environments.

#### Why This Structure?

| Common First Impression          | Actual Intent                                                         |
| -------------------------------- | --------------------------------------------------------------------- |
| "Over-engineered"                | Deliberate vertical slice architecture for scalable team development  |
| "Too many files"                 | Proper separation of concerns – each file has a single responsibility |
| "Java patterns forced on Python" | Framework-agnostic patterns that work in any language                 |

#### Design Decisions

1. **Vertical Slices over Horizontal Layers**

   - Each feature (`createOrder`, `processOrder`) is a self-contained unit
   - Adding a new feature = adding a new folder, not modifying existing code
   - Deleting a feature = deleting a folder, with zero impact on other features

2. **Explicit Naming Convention**

   - `USECASE_`, `SERVICE_`, `CONTRACT_`, `INTERFACE_` prefixes make the role of each class immediately clear
   - Self-documenting code that scales across large teams
   - Enables quick navigation in large codebases

3. **Dependency Inversion**

   - Business logic (use cases) depends on abstractions (interfaces), not implementations
   - Contracts can be swapped without changing business logic (e.g., `V0` → `V1`)
   - Enables easy testing with mock implementations

This architecture is particularly suited for:

- 🏢 **Enterprise microservices** with multiple teams
- 🔄 **Polyglot environments** (same patterns in Python, Java, TypeScript)
- 📈 **Rapidly evolving products** where features are added/removed frequently

---

### Clean Architecture Layers

This service follows a **Clean Architecture** pattern:

| Layer          | Description                                                       |
| -------------- | ----------------------------------------------------------------- |
| **Entities**   | Domain models (e.g., `Order`, `OrderItem`, `Event`)               |
| **Use Cases**  | Business logic (e.g., `USECASE_CreateOrder`)                      |
| **Interfaces** | Abstract contracts (e.g., `INTERFACE_HELPER_CreateOrder`)         |
| **Contracts**  | Concrete implementations (e.g., `CONTRACT_HELPER_CreateOrder_V0`) |
| **Services**   | Orchestrators that wire use cases with helpers                    |
| **Spring**     | Spring beans for dependency injection (e.g., `BEAN_CreateOrder`)  |

### Order Creation Workflow

The `USECASE_CreateOrder` executes the following steps:

1. **Validate Input** – Check customer ID, items, currency, and item details
2. **Build Order** – Create Order entity with OrderItems, calculate totals
3. **Save Order** – Persist to database via helper
4. **Publish Event** – Send OrderCreated event to Kafka
5. **Return Output** – Return order details to caller

## Event Flow

```
┌─────────────────┐     ┌────────────────────┐     ┌─────────────────────┐
│ OrdersController│────▶│ SERVICE_CreateOrder│────▶│ USECASE_CreateOrder │
│ POST /create    │     │  (orchestrator)    │     │  (business logic)   │
└─────────────────┘     └────────────────────┘     └─────────────────────┘
                                                            │
                                                            ▼
                        ┌─────────────────────────────────────────────────┐
                        │  CONTRACT_HELPER_CreateOrder_V0                 │
                        │  ├─▶ saveOrder(order)                           │
                        │  └─▶ publishEvent(OrderCreated)                 │
                        └─────────────────────────────────────────────────┘
                                                            │
                                                            ▼
                                                ┌─────────────────────┐
                                                │  Kafka Topic        │
                                                │  (order-events)     │
                                                └─────────────────────┘
```

## Kafka Event Schema

Events published to Kafka follow this structure:

```json
{
  "id": "550e8400-e29b-41d4-a716-446655440000",
  "name": "OrderCreated",
  "data": {
    "orderId": "12345",
    "customerId": "cust-789",
    "totalAmount": 150.0,
    "currency": "INR",
    "status": "CREATED",
    "items": [
      {
        "productId": "prod-001",
        "quantity": 2,
        "unitPrice": 75.0,
        "totalPrice": 150.0
      }
    ],
    "createdAt": "2026-01-05T11:34:53.145Z"
  }
}
```

| Field              | Type   | Description                                       |
| ------------------ | ------ | ------------------------------------------------- |
| `id`               | UUID   | Unique event identifier                           |
| `name`             | string | Event type (e.g., `OrderCreated`, `OrderShipped`) |
| `data`             | object | Event-specific payload                            |
| `data.orderId`     | string | Order identifier                                  |
| `data.customerId`  | string | Customer identifier                               |
| `data.totalAmount` | float  | Order total                                       |
| `data.currency`    | string | Currency code (ISO 4217)                          |

## Failure Handling & Reliability

### Implemented Failsafe Mechanisms

| Mechanism                   | Implementation                                                  |
| --------------------------- | --------------------------------------------------------------- |
| **Connection Retry**        | 5 attempts with exponential backoff (1s → 2s → 4s → 8s → 16s)   |
| **Correlation IDs**         | UUID generated per message for distributed tracing `[abc12345]` |
| **Safe JSON Parsing**       | Invalid JSON caught and routed to DLQ                           |
| **Double-Close Prevention** | Consumer closed flag prevents errors                            |
| **DLQ with Metadata**       | Headers include `correlation_id` and `error_reason`             |

### Future Enhancements

| Enhancement           | Description                                     |
| --------------------- | ----------------------------------------------- |
| Health Check Endpoint | Kubernetes readiness probe integration          |
| Manual Offset Commit  | At-least-once delivery guarantee                |
| Circuit Breaker       | Prevent cascade failures to downstream services |
| Consumer Scaling      | Horizontal scaling via topic partitioning       |

## Interview Talking Points

<details>
<summary>Click to expand prepared answers for common interview questions</summary>

### "Describe the architecture"

> "This is a Java Spring Boot service that creates orders and publishes events. Domain logic is isolated using Clean Architecture with Vertical Slices. It uses Spring Data JPA for persistence and Spring Kafka for event publishing."

### "Why Clean Architecture?"

> "It enables independent testing of business rules, isolates side-effects, and makes it easy to swap implementations (e.g., Kafka → RabbitMQ, H2 → PostgreSQL) without changing core logic. Each feature is a self-contained vertical slice that can be developed, tested, and deployed independently."

### "How would you scale this?"

> "For the producer service, I'd scale horizontally behind a load balancer. For the database, I'd use connection pooling and potentially read replicas. For Kafka, I'd ensure proper partitioning based on order ID to allow parallel consumption while maintaining order guarantees."

### "What happens if Kafka is down?"

> "Currently, the service might fail to publish. In production, I'd implement an outbox pattern: save the event to the database in the same transaction as the order, then have a separate background worker publish events to Kafka. This ensures data consistency even if the broker is unavailable."

</details>

## Naming Conventions

| Type      | Pattern                                        | Example                          |
| --------- | ---------------------------------------------- | -------------------------------- |
| Use Case  | `USECASE_<FeatureName>`                        | `USECASE_CreateOrder`            |
| Interface | `INTERFACE_HELPER_<FeatureName>`               | `INTERFACE_HELPER_CreateOrder`   |
| Contract  | `CONTRACT_HELPER_<FeatureName>_V<N>`           | `CONTRACT_HELPER_CreateOrder_V0` |
| Schema    | `INPUT_<FeatureName>` / `OUTPUT_<FeatureName>` | `INPUT_CreateOrder`              |
| Service   | `SERVICE_<FeatureName>`                        | `SERVICE_CreateOrder`            |
| Bean      | `BEAN_<FeatureName>`                           | `BEAN_CreateOrder`               |
| Exception | `<Name>Exception`                              | `InvalidOrderException`          |

## Related Services

- **order-processing-service** – Python service that consumes order events from Kafka and processes them

## License

MIT
