# Session 3: Who Owns the Main Loop - Java

> Spring Boot: Servlet Filters, HandlerInterceptors, `@ControllerAdvice`, lifecycle hooks

## Prerequisites

- Java 21+
- Maven

## Project Structure

```
java/
├── src/main/java/coffeeshop/
│   ├── CoffeeShopApplication.java          # Spring Boot entry point
│   ├── order/                              # Order bounded context
│   │   ├── Order.java
│   │   ├── OrderRepository.java            # Interface
│   │   ├── InMemoryOrderRepository.java    # @Repository
│   │   ├── OrderService.java               # @Service
│   │   └── OrderController.java            # @RestController
│   ├── menu/                               # Menu bounded context
│   │   ├── MenuItem.java
│   │   ├── MenuRepository.java
│   │   ├── InMemoryMenuRepository.java
│   │   ├── MenuService.java
│   │   └── MenuController.java
│   ├── kitchen/                            # Kitchen bounded context
│   │   ├── Preparation.java
│   │   └── KitchenService.java
│   ├── notification/                       # Notification bounded context
│   │   └── NotificationService.java
│   ├── middleware/                          # Request pipeline
│   │   ├── RequestLoggingFilter.java       # Servlet Filter (outermost)
│   │   ├── TimingInterceptor.java          # HandlerInterceptor (Spring MVC level)
│   │   └── WebConfig.java                  # Interceptor registration
│   ├── error/
│   │   └── GlobalExceptionHandler.java     # @ControllerAdvice (error handling)
│   └── lifecycle/
│       └── StartupShutdownHooks.java       # @PostConstruct, @PreDestroy, events
└── pom.xml
```

## How to Run

```bash
mvn spring-boot:run
```

Server starts on http://localhost:8080.

- `GET /orders` - list all orders
- `GET /orders/1` - get order by ID
- `GET /menu` - list menu items

Watch the console output: you'll see the middleware pipeline in action.

## What to Look At

- **`middleware/RequestLoggingFilter.java`** - A `javax.servlet.Filter`. This is the outermost middleware layer. It wraps every request. `chain.doFilter()` passes control to the
  next filter or to the DispatcherServlet. The "before" code runs before your handler, the "after" code runs after.
- **`middleware/TimingInterceptor.java`** - A Spring MVC `HandlerInterceptor`. This runs inside Spring's dispatcher, after filters but before/after your controller. `preHandle()`
  runs before, `afterCompletion()` runs after. Returning `false` from `preHandle()` short-circuits the request.
- **`error/GlobalExceptionHandler.java`** - `@ControllerAdvice` catches exceptions thrown by any controller. The framework catches the exception, finds the matching handler, and
  returns a proper HTTP response. You don't need try/catch in your controllers.
- **`lifecycle/StartupShutdownHooks.java`** - `@PostConstruct` runs after DI is complete. `ApplicationReadyEvent` fires when the server is ready. `@PreDestroy` runs on shutdown.

## Spring Boot Request Pipeline Deep Dive

### The Layers (Outermost to Innermost)

1. **Tomcat** - accepts TCP, parses HTTP, manages thread pool
2. **Servlet Filters** - `RequestLoggingFilter`, Spring Security filters, CORS
3. **DispatcherServlet** - Spring MVC's front controller, does route matching
4. **HandlerInterceptors** - `preHandle()` -> your controller -> `postHandle()` -> `afterCompletion()`
5. **Argument Resolution** - `@PathVariable`, `@RequestBody`, Jackson deserialization
6. **Your Controller** - the actual business logic
7. **Response Serialization** - Jackson converts return value to JSON
8. **Exception Handling** - `@ControllerAdvice` catches errors

### Filter vs Interceptor: When to Use Which

| Aspect             | Servlet Filter              | HandlerInterceptor             |
|--------------------|-----------------------------|--------------------------------|
| Layer              | Servlet container (Tomcat)  | Spring MVC                     |
| Runs for           | All HTTP requests           | Only matched controller routes |
| Access to Spring   | Limited                     | Full (handler method, model)   |
| Can modify request | Yes (wrapping)              | Yes (attributes)               |
| Can short-circuit  | Don't call `chain.doFilter` | Return `false` from preHandle  |
| Use for            | CORS, security, logging     | Auth checks, timing, auditing  |

### Error Handling Flow

1. Controller throws `IllegalArgumentException`
2. Spring catches it (your code doesn't need try/catch)
3. Spring looks for `@ExceptionHandler(IllegalArgumentException.class)` in `@ControllerAdvice`
4. The handler returns `ResponseEntity` with status 400 and error body
5. Spring serializes the response and sends it back through the filter chain

## Exercises

See the [session README](../README.md#try-it-yourself) for the full exercise list. In this Java project:

1. Run the app, hit `GET /orders`, and read the console output. Trace the filter -> interceptor -> controller -> interceptor -> filter flow.
2. Add a request ID filter that generates a UUID, adds it to the response header `X-Request-Id`, and includes it in all log messages.
3. Add an auth interceptor that checks for an `Authorization` header. Return 401 if missing. Verify your controller does NOT run.
4. Hit `GET /orders/999`. See the 404 response. Now make `OrderController` throw an `IllegalArgumentException` for invalid IDs and watch the `@ControllerAdvice` handle it.
5. Add a `@PostConstruct` to `OrderService` that logs "OrderService initialized with N orders". Verify it runs before the server is ready.

> **Solutions:** See the [`solutions/session-3/java`](https://github.com/NazarUsik/under-the-hood-workshop/tree/solutions/session-3/java) branch.
