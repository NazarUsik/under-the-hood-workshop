# Session 2: Dependency Injection - Java

> Spring Boot: `@Service`, `@Repository`, constructor injection, and a DIY container

## Prerequisites

- Java 21+
- Maven 3.9+

## Project Structure

```
java/
├── src/main/java/coffeeshop/
│   ├── CoffeeShopApplication.java         # Spring Boot entry point
│   ├── order/
│   │   ├── Order.java                     # Domain record
│   │   ├── OrderRepository.java           # Interface (the abstraction)
│   │   ├── InMemoryOrderRepository.java   # @Repository (the implementation)
│   │   ├── OrderService.java              # @Service (depends on OrderRepository)
│   │   └── OrderController.java           # @RestController (depends on OrderService)
│   └── container/
│       ├── Container.java                 # DIY DI container (~40 lines)
│       └── ContainerDemo.java             # Run this to see the custom container in action
└── pom.xml
```

## How to Run

**Spring Boot app:**

```bash
mvn spring-boot:run
```

Server starts on http://localhost:8080.

- `GET /orders` - list all orders
- `GET /orders/1` - get order by ID

**Custom DI container demo:**

```bash
mvn compile -q
java -cp target/classes coffeeshop.container.ContainerDemo
```

## What to Look At

- **`OrderRepository.java`** - An interface. No implementation details. This is what `OrderService` depends on.
- **`InMemoryOrderRepository.java`** - Annotated `@Repository`. Spring finds this during component scanning and registers it as the bean for `OrderRepository`.
- **`OrderService.java`** - Takes `OrderRepository` via constructor. No `@Autowired` needed: since Spring 4.3, a single constructor is auto-wired implicitly.
- **`OrderController.java`** - Takes `OrderService` via constructor. The full chain is wired by Spring: Controller -> Service -> Repository.
- **`container/Container.java`** - A minimal DI container that does what Spring does: register types, walk the dependency graph via reflection, detect circular dependencies, and
  create instances in the right order.

## Spring Deep Dive

### BeanFactory vs ApplicationContext

Spring has two container interfaces. `BeanFactory` is the base: it lazily creates beans on first request. `ApplicationContext` extends it with eager initialization, event
publishing, environment abstraction, and internationalization. In practice, you always use `ApplicationContext` (via `SpringApplication.run()`), but understanding `BeanFactory`
helps you see that the core is just a registry + factory.

### How Component Scanning Works

When `@SpringBootApplication` starts, it triggers `@ComponentScan` on the package where the main class lives. Spring uses ASM (a bytecode library) to read class metadata without
loading classes into the JVM. It finds every class annotated with `@Component` (or meta-annotations like `@Service`, `@Repository`, `@Controller`), creates a `BeanDefinition` for
each, and registers them in the `BeanFactory`.

This is why your `@Service` must be in the same package (or a sub-package) as your `@SpringBootApplication`. If it's outside the scan path, Spring doesn't know it exists.

### @Primary vs @Qualifier

When you have two beans of the same type:

```java

@Repository
@Primary
public class InMemoryOrderRepository implements OrderRepository { ...
}

@Repository
@Qualifier("postgres")
public class PostgresOrderRepository implements OrderRepository { ...
}
```

- `@Primary` makes `InMemoryOrderRepository` the default. Any constructor asking for `OrderRepository` gets this one.
- `@Qualifier("postgres")` lets you request a specific bean by name: `public OrderService(@Qualifier("postgres") OrderRepository repo)`.
- `@Profile("prod")` / `@Profile("test")` activates beans conditionally based on the active Spring profile.

### Bean Lifecycle Hooks

Spring beans aren't just created and injected. They go through a lifecycle:

1. Instantiation (constructor call)
2. Dependency injection (setter/field injection, if any)
3. `@PostConstruct` method (your initialization logic)
4. Ready to use
5. `@PreDestroy` method (cleanup on shutdown)

`@PostConstruct` is useful for initialization that needs all dependencies to be injected first (e.g., loading cache, validating config).

### Auto-Configuration

Spring Boot's "magic" comes from `spring-boot-autoconfigure`. It ships hundreds of `@Configuration` classes guarded by `@ConditionalOnClass`, `@ConditionalOnMissingBean`, etc. When
you add `spring-boot-starter-web` to your classpath, Spring Boot detects Tomcat on the classpath and auto-configures an embedded web server. You didn't write a single line of
server config. That's auto-configuration: conditional bean registration based on what's on the classpath.

## Exercises

See the [session README](../README.md#try-it-yourself) for the full exercise list. In this Java project:

1. Run the app and trace the DI chain: `GET /orders` -> `OrderController` -> `OrderService` -> `InMemoryOrderRepository`
2. Add a `MenuService` + `MenuRepository` + `MenuController` and expose `GET /menu`
3. Create a `PostgresOrderRepository` (just return different data) and use `@Primary` or `@Qualifier` to swap implementations
4. Write a unit test for `OrderService` with a mock repository (no Spring context needed)
5. Run `ContainerDemo` and study `Container.java` - extend it with scope support (singleton vs prototype)

> **Solutions:** See the [`solutions/session-2/java`](https://github.com/NazarUsik/under-the-hood-workshop/tree/solutions/session-2/java) branch.
