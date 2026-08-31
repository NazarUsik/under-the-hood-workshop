# Session 5: Chaos Engineering - TypeScript

> NestJS: Module-level DI swap, chaos injection via env var, resilience wrappers (timeout + fallback)

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
│   │   ├── order.service.ts                 # Calls KitchenService.prepare()
│   │   ├── order.controller.ts              # POST /orders/:id/prepare triggers chaos
│   │   └── order.module.ts                  # Imports KitchenModule
│   ├── menu/
│   │   ├── menu.model.ts / menu.repository.ts / menu.service.ts
│   │   ├── menu.controller.ts
│   │   └── menu.module.ts
│   ├── kitchen/
│   │   ├── kitchen.service.ts              # Abstract + Real + Chaos implementations
│   │   └── kitchen.module.ts              # DI swap via useFactory (chaos) or useClass (normal)
│   └── resilience/
│       ├── timeout.service.ts              # TimeoutKitchenService wrapper
│       └── fallback.service.ts             # FallbackKitchenService wrapper
├── package.json
└── tsconfig.json
```

## How to Run

**Normal mode** (everything works):

```bash
npm install
npm start
```

**Chaos mode** (random failures):

```bash
npm run start:chaos
```

Server starts on http://localhost:8080.

- `GET /orders` - list all orders
- `GET /orders/1` - get order by ID
- `POST /orders/1/prepare` - prepare an order (chaos happens here)
- `GET /menu` - list menu items

## How the DI Swap Works

NestJS module providers control which implementation gets injected:

```typescript
const kitchenProvider = chaosMode
    ? {
        provide: KitchenService,
        useFactory: () => {
            const chaos = new ChaosKitchenService();
            const withTimeout = new TimeoutKitchenService(chaos, 3000);
            return new FallbackKitchenService(withTimeout);
        },
    }
    : {provide: KitchenService, useClass: RealKitchenService};
```

`OrderService` injects `KitchenService` via the constructor. It doesn't know which implementation it gets.

## What to Look At

- **`kitchen/kitchen.service.ts`** - `KitchenService` abstract class, `RealKitchenService`, `ChaosKitchenService` (30% crash, 20% latency, 10% bad data, 40% success).
- **`kitchen/kitchen.module.ts`** - the DI swap. `CHAOS_MODE` env var controls which provider chain gets registered.
- **`resilience/timeout.service.ts`** - checks elapsed time after the call; throws if it exceeds the limit.
- **`resilience/fallback.service.ts`** - catches exceptions, returns `{status: "queued"}`.

## Exercises

See the [session README](../README.md#try-it-yourself) for the full exercise list. In this TypeScript project:

1. Run in normal mode. `POST /orders/1/prepare` always returns `"preparing"`.
2. Run in chaos mode. Hit `POST /orders/1/prepare` 20 times. Count successes, failures, and "queued" fallbacks.
3. Add a `RetryKitchenService` wrapper that retries up to 3 times on failure before giving up.
4. Add a `CircuitBreakerKitchenService` that opens after 3 consecutive failures and stays open for 10 seconds.
5. Stack them in `kitchen.module.ts`: `Fallback(Retry(CircuitBreaker(Timeout(chaos))))`.

> **Solutions:** See the [`solutions/session-5/typescript`](https://github.com/NazarUsik/under-the-hood-workshop/tree/solutions/session-5/typescript) branch.
