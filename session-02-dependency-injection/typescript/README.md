# Session 2: Dependency Injection - TypeScript

> NestJS: `@Injectable()`, module providers, `reflect-metadata`, and a DIY container

## Prerequisites

- Node.js 20+
- npm

## Project Structure

```
typescript/
├── src/
│   ├── main.ts                      # NestJS bootstrap
│   ├── app.module.ts                # Root module (imports OrderModule)
│   ├── order/
│   │   ├── order.model.ts           # Order interface
│   │   ├── order.repository.ts      # Abstract class + InMemory implementation
│   │   ├── order.service.ts         # @Injectable() (depends on OrderRepository)
│   │   ├── order.controller.ts      # @Controller (depends on OrderService)
│   │   └── order.module.ts          # Module wiring: providers + controllers
│   └── container/
│       ├── container.ts             # DIY DI container (~30 lines)
│       └── demo.ts                  # Run this to see the custom container
├── package.json
└── tsconfig.json
```

## How to Run

**NestJS app:**

```bash
npm install
npm start
```

Server starts on http://localhost:8080.

- `GET /orders` - list all orders
- `GET /orders/1` - get order by ID

**Custom DI container demo:**

```bash
npm run container:demo
```

## What to Look At

- **`order/order.repository.ts`** - An abstract class (not an interface!) used as both a type and a DI token. TypeScript interfaces are erased at runtime, so NestJS can't use them
  for injection. Abstract classes survive compilation.
- **`order/order.service.ts`** - `@Injectable()` marks this for DI. The constructor takes `OrderRepository` and NestJS injects the matching provider.
- **`order/order.module.ts`** - The wiring happens here: `{provide: OrderRepository, useClass: InMemoryOrderRepository}`. To swap implementations, change `useClass`. Nothing else
  changes.
- **`container/container.ts`** - Uses `Reflect.getMetadata("design:paramtypes", ...)` to read constructor types at runtime. This is exactly what NestJS does internally.

## NestJS Deep Dive

### How reflect-metadata Works

When `emitDecoratorMetadata` is enabled in `tsconfig.json`, TypeScript emits metadata about constructor parameter types. NestJS reads this at runtime with
`Reflect.getMetadata("design:paramtypes", SomeClass)` to discover what each class needs.

This is why `@Injectable()` exists: it's a decorator that triggers metadata emission. Without it, TypeScript doesn't emit the type info, and NestJS can't resolve dependencies.

### Why Abstract Classes Instead of Interfaces

TypeScript interfaces don't exist at runtime. They're erased during compilation. So this doesn't work:

```typescript
// NestJS can't use this as an injection token
interface OrderRepository { ... }
```

Instead, use abstract classes:

```typescript
@Injectable()
export abstract class OrderRepository {
    abstract findAll(): Order[];
}
```

Or use string/symbol tokens:

```typescript
@Module({
    providers: [{ provide: 'ORDER_REPO', useClass: InMemoryOrderRepository }],
})
```

Then inject with `@Inject('ORDER_REPO')`.

### Custom Providers

NestJS supports four provider types:

- **`useClass`** - instantiate a class: `{provide: OrderRepository, useClass: InMemoryOrderRepository}`
- **`useValue`** - provide a constant: `{provide: 'API_KEY', useValue: 'abc123'}`
- **`useFactory`** - call a function: `{provide: OrderRepository, useFactory: () => new InMemoryOrderRepository()}`
- **`useExisting`** - alias another provider: `{provide: 'ALIAS', useExisting: OrderRepository}`

### Module Scoping

NestJS modules create DI scope boundaries. A provider registered in `OrderModule` is only available to classes in that module unless explicitly exported:

```typescript
@Module({
    providers: [OrderService],
    exports: [OrderService],  // now other modules can inject OrderService
})
```

This is similar to Spring's `@ComponentScan` boundaries but more explicit.

### Scopes

By default, NestJS providers are singletons. You can change this:

```typescript
@Injectable({ scope: Scope.REQUEST })   // new instance per HTTP request
@Injectable({ scope: Scope.TRANSIENT }) // new instance every injection
```

Request scope is useful for per-request state (user context, transactions). Transient is useful for stateful helpers that shouldn't be shared.

## Exercises

See the [session README](../README.md#try-it-yourself) for the full exercise list. In this TypeScript project:

1. Run the app and trace the DI chain: `GET /orders` -> `OrderController` -> `OrderService` -> `InMemoryOrderRepository`
2. Add a `MenuModule` with `MenuService`, `MenuRepository`, `MenuController` and expose `GET /menu`
3. Create a `PostgresOrderRepository` and swap it in `order.module.ts` via `useClass`
4. Write a unit test for `OrderService` with a stub repository (no NestJS testing module needed)
5. Run `npm run container:demo` and study `container.ts` - extend it with scope support

> **Solutions:** See the [`solutions/session-2/typescript`](https://github.com/NazarUsik/under-the-hood-workshop/tree/solutions/session-2/typescript) branch.
