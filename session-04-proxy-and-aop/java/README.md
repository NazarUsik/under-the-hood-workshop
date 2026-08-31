# Session 4: Proxy Pattern and AOP - Java

> Spring Boot: Spring AOP, `@Aspect`, `@Around`, CGLIB proxies, pointcut expressions

## Prerequisites

- Java 21+
- Maven 3.9+

## Project Structure

```
java/
├── src/main/java/coffeeshop/
│   ├── CoffeeShopApplication.java          # Spring Boot entry point
│   ├── order/
│   │   ├── Order.java                      # Domain model
│   │   ├── OrderRepository.java            # Repository interface
│   │   ├── InMemoryOrderRepository.java    # In-memory implementation
│   │   ├── OrderService.java               # Service (NO logging/timing code!)
│   │   └── OrderController.java            # REST controller
│   ├── menu/
│   │   ├── MenuItem.java                   # Domain model
│   │   ├── MenuRepository.java             # Repository interface
│   │   ├── InMemoryMenuRepository.java     # In-memory implementation
│   │   ├── MenuService.java                # Service (clean, no cross-cutting code)
│   │   └── MenuController.java             # REST controller
│   ├── kitchen/
│   │   ├── Preparation.java                # Domain model
│   │   └── KitchenService.java             # Service
│   ├── notification/
│   │   └── NotificationService.java        # Service
│   └── aspect/
│       ├── LoggingAspect.java              # Logs method calls, args, return values
│       ├── TimingAspect.java               # Measures method execution time
│       └── AuditAspect.java                # Audit trail with @Before, @AfterReturning, @AfterThrowing
├── src/main/resources/application.properties
└── pom.xml
```

## How to Run

```bash
./mvnw spring-boot:run
```

Server starts on http://localhost:8080.

- `GET /orders` - list all orders
- `GET /orders/1` - get order by ID
- `GET /menu` - list menu items

Watch the console: you'll see `[Logging]`, `[Timing]`, and `[Audit]` messages even though the service code has zero logging.

## What to Look At

The key insight: **look at `OrderService.java` and `MenuService.java`**. They have no logging, no timing, no audit code. They're pure business logic. Then look at the console
output when you hit an endpoint. All that observability comes from the aspects.

- **`aspect/LoggingAspect.java`** - `@Around` advice on all `*Service` methods. `joinPoint.proceed()` calls the real method. Everything before it is "before", everything after is
  "after". `@Order(1)` makes it the outermost proxy.
- **`aspect/TimingAspect.java`** - Another `@Around` advice. `@Order(2)` puts it inside the logging proxy. The timing measurement excludes the logging overhead.
- **`aspect/AuditAspect.java`** - Uses `@Before`, `@AfterReturning`, and `@AfterThrowing` as separate advice types (instead of one `@Around`). Targets only `OrderService`.

## How Spring AOP Creates Proxies

When Spring sees `@Aspect`, it does the following at startup:

1. Scans all beans and all aspect pointcut expressions
2. For each bean that matches a pointcut, creates a **CGLIB proxy** (a generated subclass)
3. Registers the proxy in the container *instead of* the real bean
4. When you inject `OrderService`, you actually get the proxy

So when `OrderController` calls `orderService.listOrders()`:

```
OrderController
  └─▶ CGLIB Proxy (looks like OrderService)
        ├─▶ LoggingAspect.logMethodCall()      [@Order(1), before proceed]
        │     ├─▶ TimingAspect.timeMethodCall() [@Order(2), before proceed]
        │     │     ├─▶ AuditAspect.auditBefore()
        │     │     ├─▶ OrderService.listOrders()    ← the real method
        │     │     ├─▶ AuditAspect.auditAfterReturning()
        │     │     └─▶ TimingAspect logs duration
        │     └─▶ LoggingAspect logs result
        └─▶ response returned to controller
```

### Verifying It's a Proxy

Add this to any controller to see the proxy class name:

```java
System.out.println(orderService.getClass().getName());
// Prints: coffeeshop.order.OrderService$$SpringCGLIB$$0
```

The `$$SpringCGLIB$$` suffix proves it's a generated proxy, not the real class.

## CGLIB Proxy Deep Dive

### What CGLIB Actually Does

CGLIB (Code Generation Library) generates a **subclass** of your bean at runtime using bytecode manipulation. The generated class overrides every non-final method and inserts the
advice chain before delegating to `super`:

```java
// What CGLIB generates (simplified pseudocode):
class OrderService$$SpringCGLIB$$0 extends OrderService {

    private MethodInterceptor interceptor; // holds the aspect chain

    @Override
    public List<Order> listOrders() {
        // interceptor runs: LoggingAspect -> TimingAspect -> AuditAspect
        // then calls super.listOrders() via reflection
        return (List<Order>) interceptor.intercept(
            this, listOrdersMethod, new Object[]{}, methodProxy);
    }
}
```

The proxy IS-A `OrderService` (it extends it), so `instanceof` checks pass, and Spring can inject it anywhere an `OrderService` is expected.

### JDK Dynamic Proxy vs CGLIB

Spring has two proxy strategies:

|                         | JDK Dynamic Proxy                                  | CGLIB                                |
|-------------------------|----------------------------------------------------|--------------------------------------|
| **Mechanism**           | Implements interface via `java.lang.reflect.Proxy` | Generates subclass via bytecode      |
| **Requires**            | Bean must implement an interface                   | No interface needed                  |
| **Speed**               | Slightly slower (reflection)                       | Slightly faster (generated bytecode) |
| **Spring Boot default** | Not default since Spring Boot 2.0                  | **Default**                          |

Spring Boot uses CGLIB by default (`spring.aop.proxy-target-class=true`). You can force JDK proxies with `spring.aop.proxy-target-class=false`, but then only methods declared in
interfaces get proxied.

### The Self-Invocation Trap

This is the single most common AOP pitfall. When a method calls another method on the same object, the proxy is bypassed:

```java

@Service
public class OrderService {

    public List<Order> listOrders() {
        // This calls findOrder() directly on 'this' (the real object),
        // NOT on the proxy. Aspects on findOrder() will NOT run.
        Order first = findOrder(1).orElse(null);
        return repository.findAll();
    }

    public Optional<Order> findOrder(int id) {
        return repository.findById(id);
    }
}
```

Why? Because `this` inside `listOrders()` refers to the real `OrderService` instance, not the CGLIB proxy. The proxy only intercepts calls that come from outside the object.

```
External call:    Controller ──▶ Proxy ──▶ listOrders()     ✅ aspects run
Self-invocation:  listOrders() ──▶ this.findOrder()          ❌ aspects skipped
```

**Workarounds:**

1. **Inject self** - inject the bean into itself (Spring resolves it to the proxy):
   ```java
   @Lazy @Autowired private OrderService self;

   public List<Order> listOrders() {
       self.findOrder(1); // goes through proxy, aspects run
   }
   ```

2. **Restructure** - move the called method to a different bean so the call goes through the proxy.

3. **Use `AopContext`** - `((OrderService) AopContext.currentProxy()).findOrder(1)`. Requires `@EnableAspectJAutoProxy(exposeProxy = true)`. Fragile and not recommended.

### Bean Identity vs Proxy Identity

The proxy and the real object are different instances:

```java

@Autowired
private OrderService orderService; // this is the PROXY

// In a @PostConstruct or test:
System.out.println(orderService.getClass());
// coffeeshop.order.OrderService$$SpringCGLIB$$0
System.out.println(orderService instanceof OrderService);
// true (CGLIB extends your class)

// But the proxy is NOT the same object as the real bean:
// proxy.equals(realBean) depends on your equals() implementation
// proxy == realBean is ALWAYS false
```

This matters when you store references, use identity checks, or rely on `equals()`/`hashCode()` that use the object's fields. The proxy delegates method calls, but its own fields
are empty (they belong to the subclass, not the real instance).

### Final Methods and Classes

CGLIB generates a subclass, so it **cannot override final methods or extend final classes**:

```java
// This method will NOT be proxied. No aspects will run on it.
// No error, no warning. It just silently doesn't work.
public final List<Order> listOrders() {
    return repository.findAll();
}

// This class cannot be proxied at all. Spring will throw an error at startup
// if an aspect tries to target a final class.
public final class OrderService { ...
}
```

**Rule of thumb:** never make `@Service`, `@Component`, or `@Repository` classes or their methods `final` if you use AOP. Kotlin users: Kotlin classes are `final` by default. Use
the `kotlin-spring` compiler plugin (included in Spring Boot's Kotlin support) to automatically open Spring-annotated classes.

### Proxy Limitations with Reactive Programming

Spring AOP proxies are synchronous. They wrap the method call and wait for it to return. This works fine for blocking code, but breaks with reactive types:

```java

@Around("execution(* coffeeshop..*Service.*(..))")
public Object logMethodCall(ProceedingJoinPoint joinPoint) throws Throwable {
    long start = System.currentTimeMillis();
    Object result = joinPoint.proceed();
    // BAD: if result is a Mono/Flux, it hasn't executed yet!
    // You're measuring assembly time, not execution time.
    long duration = System.currentTimeMillis() - start;
    System.out.println("Took " + duration + "ms"); // always ~0ms
    return result;
}
```

With reactive types (`Mono`, `Flux`), `proceed()` returns immediately with a *cold publisher*. The actual work happens later when someone subscribes. To measure real execution
time, you need to hook into the reactive chain:

```java
Object result = joinPoint.proceed();
if(result instanceof Mono<?> mono) {
    return mono.doOnSubscribe(s ->...)
                .doOnTerminate(() ->...);
}
```

This project uses blocking Spring MVC, so standard `@Around` works perfectly. But if you move to WebFlux, your aspects need to become reactive-aware.

### Circular Dependency Resolution Using Early Proxies

Spring can resolve circular dependencies (A depends on B, B depends on A) for singleton beans through a three-level cache:

1. **singletonObjects** - fully initialized beans
2. **earlySingletonObjects** - partially initialized beans (constructor done, properties not set)
3. **singletonFactories** - factory lambdas that can wrap a bean in a proxy

When A is being created and needs B, but B also needs A, Spring exposes an "early reference" to A from level 3. If A needs to be proxied, the factory at level 3 creates the proxy
early, before A is fully initialized. B gets the proxy of A, then A finishes initialization.

This is why constructor injection can fail with circular dependencies (the constructor hasn't finished, so there's no instance to expose), but field/setter injection works (the
constructor finishes first, exposing the early reference).

**Best practice:** avoid circular dependencies entirely. If you have them, it's usually a sign that two beans should be merged or a third bean should be extracted.

## Pointcut Expression Cheat Sheet

| Expression                                                | Matches                               |
|-----------------------------------------------------------|---------------------------------------|
| `execution(* coffeeshop..*Service.*(..))`                 | Any method in any `*Service` class    |
| `execution(public * coffeeshop.order.OrderService.*(..))` | Only public methods in `OrderService` |
| `@annotation(coffeeshop.aspect.Timed)`                    | Methods annotated with `@Timed`       |
| `within(coffeeshop.order..*)`                             | Any method in the `order` package     |
| `bean(*Service)`                                          | Any method on beans named `*Service`  |

## Exercises

See the [session README](../README.md#try-it-yourself) for the full exercise list. In this Java project:

1. Run the app and hit `GET /orders`. Read the console output. Trace the aspect chain: Logging -> Timing -> Audit -> real method -> Audit -> Timing -> Logging.
2. Create a `@Cacheable`-like aspect: cache the result of `MenuService.listItems()` so the repository is only called once. Hint: store the result in a `Map` keyed by method name.
3. Create a validation aspect for `OrderService.findOrder()`: check that the `id` argument is > 0, throw `IllegalArgumentException` if not. The service code stays unchanged.
4. Create a custom `@Timed` annotation and a matching aspect that uses `@annotation(Timed)` pointcut. Apply it to specific methods instead of all `*Service` methods.
5. Verify the proxy class: print `orderService.getClass().getName()` in the controller and confirm it contains `SpringCGLIB`.

> **Solutions:** See the [`solutions/session-4/java`](https://github.com/NazarUsik/under-the-hood-workshop/tree/solutions/session-4/java) branch.
