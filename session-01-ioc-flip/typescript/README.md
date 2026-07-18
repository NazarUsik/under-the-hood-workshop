# Session 1: The IoC Flip - TypeScript

> Library: Node.js http module | Framework: NestJS

## Prerequisites

- Node.js 20+
- npm

## Project Structure

```
typescript/
├── library/     # Node.js http module - you control everything
└── framework/   # NestJS - the framework calls you
```

## How to Run

**Library version:**

```bash
cd library
npm install
npm start
```

Server starts on http://localhost:8080. Try `GET /orders`.

**Framework version:**

```bash
cd framework
npm install
npm start
```

Server starts on http://localhost:8080. Try `GET /orders`.

## What to Look At

- **`library/src/main.ts`** - You call `http.createServer()`, check `req.url` and `req.method` manually, set headers, and write the response. You call `server.listen()`. Every
  decision is yours.
- **`framework/src/order/order.controller.ts`** - You decorate a class with `@Controller` and a method with `@Get()`. NestJS creates the server, configures Express underneath,
  scans your modules, and calls your method. You return data, NestJS handles the rest.
- **`framework/src/app.module.ts`** - NestJS requires you to declare modules. This is the framework owning the structure of your application, not just the HTTP layer.

## The IoC Flip in TypeScript

NestJS is heavily inspired by Angular and Spring. It uses decorators (`@Controller`, `@Get`, `@Module`) to let you declare behavior, and the framework handles the wiring.

Compare the two: the library version has `if (req.url === "/orders")` and `res.writeHead(200, ...)`. The NestJS version has `@Get()` and `return [...]`. Same result, but in the
framework version you never touch the HTTP primitives. NestJS owns the server, the routing, the serialization, and the lifecycle.

TypeScript decorators are the mechanism, IoC is the principle.

## Exercises

See the [session README](../README.md#try-it-yourself) for the full exercise list. In short:

1. Run both versions and hit `GET /orders`
2. Add `GET /menu` to both - notice the difference in effort
3. Add request logging to both
4. Throw an error from a handler and compare error behavior

> **Solutions:** See the [`solutions/session-1/typescript`](https://github.com/NazarUsik/under-the-hood-workshop/tree/solutions/session-1/typescript) branch.
