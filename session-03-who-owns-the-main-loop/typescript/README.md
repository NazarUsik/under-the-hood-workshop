# Session 3: Who Owns the Main Loop - TypeScript

> NestJS: Middleware, Interceptors, Guards, Exception Filters, lifecycle hooks

## Prerequisites

- Node.js 20+
- npm

## Project Structure

```
typescript/
├── src/
│   ├── main.ts                              # NestJS bootstrap + global interceptor/filter
│   ├── app.module.ts                        # Root module + middleware registration
│   ├── order/
│   │   ├── order.model.ts                   # Order interface
│   │   ├── order.repository.ts              # Abstract class + InMemory impl
│   │   ├── order.service.ts                 # @Injectable + lifecycle hooks
│   │   ├── order.controller.ts              # @Controller with error handling
│   │   └── order.module.ts                  # Module wiring
│   ├── menu/
│   │   ├── menu.model.ts                    # MenuItem interface
│   │   ├── menu.repository.ts               # Abstract class + InMemory impl
│   │   ├── menu.service.ts                  # @Injectable
│   │   ├── menu.controller.ts               # @Controller
│   │   └── menu.module.ts                   # Module wiring
│   └── middleware/
│       ├── logging.middleware.ts             # Express-style middleware (req, res, next)
│       ├── timing.interceptor.ts            # NestJS interceptor (wraps handler)
│       └── http-exception.filter.ts         # Exception filter (@Catch)
├── package.json
└── tsconfig.json
```

## How to Run

```bash
npm install
npm start
```

Server starts on http://localhost:8080.

- `GET /orders` - list all orders
- `GET /orders/1` - get order by ID
- `GET /menu` - list menu items

Watch the console: middleware -> interceptor -> handler -> interceptor -> middleware.

## What to Look At

- **`middleware/logging.middleware.ts`** - Express-style `(req, res, next)`. `next()` passes to the next layer. `res.on("finish")` runs after the response is sent. This is the
  outermost NestJS middleware layer.
- **`middleware/timing.interceptor.ts`** - NestJS interceptor. `next.handle()` runs the controller. `tap()` runs after. Interceptors wrap the handler; middleware wraps the entire
  request.
- **`middleware/http-exception.filter.ts`** - `@Catch(HttpException)` catches errors from any controller. The framework catches the exception and delegates to this filter.
- **`order/order.service.ts`** - `OnModuleInit` and `OnModuleDestroy` are lifecycle hooks. NestJS calls them at startup and shutdown.

## NestJS Request Pipeline Deep Dive

### The Layers (in execution order)

NestJS has a very specific execution order for its middleware-like mechanisms:

1. **Middleware** - Express-style `(req, res, next)`. Runs first. Registered in `AppModule.configure()`.
2. **Guards** - `canActivate()` returns true/false. Used for auth. Runs before interceptors.
3. **Interceptors (before)** - `intercept()` runs before `next.handle()`. Used for logging, timing, transformation.
4. **Pipes** - Transform or validate input. `@Body()`, `@Param()` values pass through pipes.
5. **Your Controller** - the actual handler method.
6. **Interceptors (after)** - The `tap()` or `map()` in the Observable chain after `next.handle()`.
7. **Exception Filters** - `@Catch()` handles errors from any of the above layers.

### Middleware vs Interceptor vs Guard

| Mechanism   | Purpose            | Has access to            | Can short-circuit? |
|-------------|--------------------|--------------------------|--------------------|
| Middleware  | General processing | `req`, `res`, `next`     | Don't call `next`  |
| Guard       | Authorization      | `ExecutionContext`       | Return `false`     |
| Interceptor | Wrap handler       | `ExecutionContext`, RxJS | Throw or empty obs |
| Pipe        | Validate/transform | Parameter value          | Throw exception    |
| Filter      | Error handling     | Exception + host         | Always terminal    |

### Lifecycle Hooks

| Hook                        | When it runs                  | Use for                    |
|-----------------------------|-------------------------------|----------------------------|
| `OnModuleInit`              | After module DI is resolved   | Init logic, cache warming  |
| `OnApplicationBootstrap`    | After all modules initialized | Cross-module setup         |
| `OnModuleDestroy`           | On `app.close()` or SIGTERM   | Cleanup, close connections |
| `BeforeApplicationShutdown` | Before connections are closed | Flush buffers, notify      |
| `OnApplicationShutdown`     | After connections closed      | Final cleanup              |

## Exercises

See the [session README](../README.md#try-it-yourself) for the full exercise list. In this TypeScript project:

1. Run the app, hit `GET /orders`, and read the console output. Trace middleware -> interceptor -> handler -> interceptor -> middleware.
2. Add a request ID middleware that sets `X-Request-Id` header on the response.
3. Add an auth guard (`@Injectable() implements CanActivate`) that checks for `Authorization` header and returns 403 if missing.
4. Hit `GET /orders/999`. See the exception filter catch the `NotFoundException`.
5. Add `OnModuleInit` to `MenuService` that logs how many menu items are loaded.

> **Solutions:** See the [`solutions/session-3/typescript`](https://github.com/NazarUsik/under-the-hood-workshop/tree/solutions/session-3/typescript) branch.
