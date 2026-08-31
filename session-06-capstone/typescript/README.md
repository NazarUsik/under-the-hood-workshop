# Session 6: Putting It All Together - TypeScript

> NestJS: all patterns combined. `applyTracing()` X-rays services at runtime. Jest tests verify the invisible.

## Prerequisites

- Node.js 18+
- npm

## Project Structure

```
typescript/
├── src/
│   ├── main.ts                              # NestJS bootstrap
│   ├── app.module.ts
│   ├── order/ menu/                         # DI: Session 2
│   ├── kitchen/
│   │   ├── kitchen.service.ts               # Abstract + Real + Chaos
│   │   └── kitchen.module.ts                # useFactory DI swap
│   ├── resilience/                          # Session 5
│   │   ├── timeout.service.ts / retry.service.ts / fallback.service.ts
│   └── tracing/                             # NEW: Session 6
│       ├── trace-collector.ts               # Step recorder
│       ├── tracing.ts                       # applyTracing(): runtime method proxy
│       └── trace.spec.ts                    # 3 trace-based Jest tests
├── package.json
└── tsconfig.json
```

## How to Run

```bash
npm install
npm start                     # normal mode
npm run start:chaos           # chaos mode
npm test                      # trace-based tests
```

## What's New in Session 6

- **`tracing/trace-collector.ts`** - step recorder, only used in tests.
- **`tracing/tracing.ts`** - `applyTracing(obj, collector)`: wraps all methods at runtime. Zero lines added to any service.
- **`tracing/trace.spec.ts`** - 3 tests: normal trace, chaos resilience trace, stress test with 20 requests.

## Exercises

1. Run `npm test`. Read the trace output for each test.
2. Remove `FallbackKitchenService` from the chaos chain in the test. Which test fails?
3. Add a rate limiter service wrapper. Write a test verifying it rejects after 10 calls.

> **Solutions:** See the [`solutions/session-6/typescript`](https://github.com/NazarUsik/under-the-hood-workshop/tree/solutions/session-6/typescript) branch.
