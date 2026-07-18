# The Domain: Coffee Shop ☕

All sessions use the same Coffee Shop domain. The business model is intentionally simple so
you can focus on the engineering patterns, not the domain logic.

## Bounded Contexts

| Context           | Core Concept                                                        | Responsibility                                      |
|-------------------|---------------------------------------------------------------------|-----------------------------------------------------|
| **Orders**        | `Order` aggregate (with `OrderItem` entities)                       | Takes orders, orchestrates fulfillment              |
| **Menu**          | `MenuItem` entity, `Category` value object                          | Catalog of drinks and food. Read-heavy, simple CRUD |
| **Kitchen**       | `Preparation` aggregate (states: pending, preparing, ready, failed) | Prepares orders. Async, can be slow, can fail       |
| **Notifications** | `Message` value object, `Channel` enum (email, sms, push)           | Tells customer their order is ready                 |

## How the Domain Grows Per Session

| Session      | What's used                                     | What's new                                                                |
|--------------|-------------------------------------------------|---------------------------------------------------------------------------|
| 1. IoC Flip  | `OrderService` only                             | Library vs framework versions of the same service                         |
| 2. DI        | `OrderService`, `MenuService`, `KitchenService` | Dependencies between services, injected not hardcoded                     |
| 3. Main Loop | All four services                               | Full request lifecycle from HTTP to "your latte is ready"                 |
| 4. Proxy/AOP | All four services                               | Audit logging, timing, validation. Added without modifying services       |
| 5. Chaos     | All four services                               | Kitchen crashes. Notifications delayed. Menu returns empty. What happens? |
| 6. Capstone  | All four services                               | Full system: DI wires it, proxies observe it, chaos breaks it             |

## API Design

REST endpoints, inspired by [Google AIP guidelines](https://google.aip.dev/):

- Resource-oriented URLs: `/orders/{id}`, `/menu/items/{id}`
- Standard methods: GET, POST, PATCH, DELETE
- Proper HTTP status codes for errors
- Consistent request/response shapes across languages

## DDD Glossary

| Term                | Meaning                                                                                     |
|---------------------|---------------------------------------------------------------------------------------------|
| **Aggregate**       | A cluster of objects treated as a single unit (e.g. `Order` + its `OrderItems`)             |
| **Entity**          | An object with identity that persists over time (e.g. `MenuItem` with an ID)                |
| **Value Object**    | An object defined by its attributes, no identity (e.g. `Category`, `Channel`)               |
| **Bounded Context** | A boundary within which a model is defined and consistent (e.g. Orders vs Kitchen)          |
| **Repository**      | Abstraction for data access (e.g. `OrderRepository`)                                        |
| **Service**         | Stateless operation that doesn't naturally belong to an entity (e.g. `NotificationService`) |
