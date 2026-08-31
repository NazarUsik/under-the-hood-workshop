# Session 4: Proxy Pattern and AOP - TypeScript

> NestJS: Interceptors as proxies, `@UseInterceptors`, RxJS pipelines, custom decorators

## Prerequisites

- Node.js 20+
- npm

## Project Structure

```
typescript/
├── src/
│   ├── main.ts                              # NestJS bootstrap
│   ├── app.module.ts                        # Root module
│   ├── order/
│   │   ├── order.model.ts                   # Order interface
│   │   ├── order.repository.ts              # Abstract + InMemory impl
│   │   ├── order.service.ts                 # Service (NO logging code!)
│   │   ├── order.controller.ts              # Controller with @UseInterceptors
│   │   └── order.module.ts                  # Module wiring
│   ├── menu/
│   │   ├── menu.model.ts                    # MenuItem interface
│   │   ├── menu.repository.ts               # Abstract + InMemory impl
│   │   ├── menu.service.ts                  # Service (clean)
│   │   ├── menu.controller.ts               # Controller with interceptors
│   │   └── menu.module.ts                   # Module wiring
│   └── proxy/
│       ├── logging.interceptor.ts           # Logs method calls, args, results
│       ├── timing.interceptor.ts            # Measures execution time
│       └── audit.interceptor.ts             # Audit trail with error tracking
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

Watch the console: `[Logging]`, `[Timing]`, and `[Audit]` messages appear even though the service code has zero logging.

## What to Look At

- **`order/order.service.ts`** and **`menu/menu.service.ts`** - pure business logic. No logging, no timing. Interceptors add it.
- **`proxy/logging.interceptor.ts`** - `next.handle()` is NestJS's `joinPoint.proceed()`. Everything before it is "before", `tap()` after is "after advice".
- **`proxy/audit.interceptor.ts`** - uses `tap()` for success and `catchError()` for failure. Same as Spring's `@AfterReturning`/`@AfterThrowing`.
- **`order/order.controller.ts`** - `@UseInterceptors(A, B, C)` stacks interceptors like Spring's `@Order`. First listed = outermost.

## NestJS Interceptors as Proxies

NestJS interceptors are the Proxy pattern implemented via RxJS Observables:

```typescript

@Injectable()
export class TimingInterceptor implements NestInterceptor {
    intercept(context: ExecutionContext, next: CallHandler): Observable<any> {
        // BEFORE: runs before the handler
        const start = Date.now();

        return next.handle().pipe(      // call the handler (like proceed())
            tap(() => {                  // AFTER: runs after the handler
                console.log(`Took ${Date.now() - start}ms`);
            }),
        );
    }
}
```

### Interceptor Stacking

```typescript
@UseInterceptors(LoggingInterceptor, TimingInterceptor, AuditInterceptor)
```

Execution order:

```
Logging.before -> Timing.before -> Audit.before -> handler -> Audit.after -> Timing.after -> Logging.after
```

First interceptor listed is outermost (runs first and last).

### Controller-Level vs Method-Level vs Global

```typescript
// All methods in this controller:
@Controller("orders")
@UseInterceptors(LoggingInterceptor)
export class OrderController {
...
}

// Only this method:
@Get()
@UseInterceptors(TimingInterceptor)
listOrders() {
    ...
}

// Every route in the app (in main.ts):
app.useGlobalInterceptors(new LoggingInterceptor());
```

### Interceptors vs Method Decorators

NestJS also supports TypeScript method decorators for AOP at the method level (not request level):

```typescript
function Timed() {
    return function (target: any, key: string, descriptor: PropertyDescriptor) {
        const original = descriptor.value;
        descriptor.value = function (...args: any[]) {
            const start = Date.now();
            const result = original.apply(this, args);
            console.log(`${key} took ${Date.now() - start}ms`);
            return result;
        };
    };
}

@Injectable()
export class OrderService {
    @Timed()
    listOrders(): Order[] {
        ...
    }
}
```

This is closer to Python's `@timed` decorator. It wraps the method at class definition time, not at request time.

## Exercises

See the [session README](../README.md#try-it-yourself) for the full exercise list. In this TypeScript project:

1. Run the app, hit `GET /orders`, and read the console output. Trace the interceptor chain: Logging -> Timing -> Audit -> handler -> Audit -> Timing -> Logging.
2. Create a `CachingInterceptor` that caches responses by URL. First request hits the handler, subsequent requests return cached data.
3. Create a `@Timed()` method decorator (not interceptor) and apply it to `OrderService.listOrders()`.
4. Move interceptors from `@UseInterceptors` on the controller to `app.useGlobalInterceptors()` in `main.ts`.
5. Create a `ValidationInterceptor` that checks the `id` param is a positive number before calling the handler.

> **Solutions:** See the [`solutions/session-4/typescript`](https://github.com/NazarUsik/under-the-hood-workshop/tree/solutions/session-4/typescript) branch.
