# Session 1: The IoC Flip - Java

> Library: JDK HttpServer | Framework: Spring Boot

## Prerequisites

- Java 21+
- Maven 3.9+

## Project Structure

```
java/
├── library/     # Plain JDK HttpServer - you control everything
└── framework/   # Spring Boot - the framework calls you
```

## How to Run

**Library version:**

```bash
cd library
mvn compile exec:java
```

Server starts on http://localhost:8080. Try `GET /orders`.

**Framework version:**

```bash
cd framework
mvn spring-boot:run
```

Server starts on http://localhost:8080. Try `GET /orders`.

## What to Look At

- **`library/src/.../Main.java`** - You create the server, bind the port, define routes manually, and start the loop yourself. Every line is your responsibility.
- **`framework/src/.../OrderController.java`** - You annotate a method with `@GetMapping` and return data. Spring Boot handles the server, routing, serialization, error handling,
  and lifecycle. You never call `main()`.
- **`framework/src/.../CoffeeShopApplication.java`** - This *is* the main class, but look at how little it does. `SpringApplication.run()` hands control to the framework.

## The IoC Flip in Java

Java makes the contrast especially clear. The JDK gives you `com.sun.net.httpserver.HttpServer`: a raw, low-level HTTP server where you manually wire handlers, set headers, write
bytes to the response stream.

Spring Boot gives you `@RestController` and `@GetMapping`. The framework creates the embedded Tomcat server, scans for your controllers, builds the routing table, and calls your
methods when requests arrive. You never touch the HTTP layer directly.

Same result. Same port. Same JSON. Completely different control flow.

## Exercises

See the [session README](../README.md#try-it-yourself) for the full exercise list. In short:

1. Run both versions and hit `GET /orders`
2. Add `GET /menu` to both - notice the difference in effort
3. Add request logging to both
4. Throw an exception from a handler and compare error behavior

> **Solutions:** See the [`solutions/session-1/java`](https://github.com/NazarUsik/under-the-hood-workshop/tree/solutions/session-1/java) branch.
