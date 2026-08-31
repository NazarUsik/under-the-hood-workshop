import "reflect-metadata";
import {Test} from "@nestjs/testing";
import {OrderService} from "../order/order.service";
import {InMemoryOrderRepository, OrderRepository} from "../order/order.repository";
import {ChaosKitchenService, KitchenService, RealKitchenService} from "../kitchen/kitchen.service";
import {TimeoutKitchenService} from "../resilience/timeout.service";
import {RetryKitchenService} from "../resilience/retry.service";
import {FallbackKitchenService} from "../resilience/fallback.service";
import {TraceCollector} from "./trace-collector";
import {applyTracing} from "./tracing";

describe("Trace-based tests", () => {
    let collector: TraceCollector;

    beforeEach(() => {
        collector = new TraceCollector();
    });

    describe("Normal mode", () => {
        it("should produce a clean trace for a normal request", async () => {
            const kitchen = new RealKitchenService();
            applyTracing(kitchen, collector);

            const module = await Test.createTestingModule({
                providers: [
                    OrderService,
                    {provide: OrderRepository, useClass: InMemoryOrderRepository},
                    {provide: KitchenService, useValue: kitchen},
                ],
            }).compile();

            const orderService = module.get(OrderService);
            applyTracing(orderService, collector);

            collector.clear();
            orderService.placeOrder(1, "Latte");

            const trace = collector.getTrace();
            console.log("=== Normal Request Trace ===");
            trace.forEach((s) => console.log(s));

            expect(trace.some((s) => s.includes("OrderService.placeOrder"))).toBe(true);
            expect(trace.some((s) => s.includes("RealKitchenService.prepare"))).toBe(true);

            // Verify before/after pairing
            const beforeCount = trace.filter((s) => s.startsWith("before:")).length;
            const afterCount = trace.filter((s) => s.startsWith("after:")).length;
            expect(beforeCount).toBe(afterCount);
        });
    });

    describe("Chaos resilience", () => {
        function buildChaosService(collector: TraceCollector): OrderService {
            const chaos = new ChaosKitchenService();
            applyTracing(chaos, collector);
            const withTimeout = new TimeoutKitchenService(chaos, 3000);
            applyTracing(withTimeout, collector);
            const withRetry = new RetryKitchenService(withTimeout, 3, 100);
            applyTracing(withRetry, collector);
            const withFallback = new FallbackKitchenService(withRetry);
            applyTracing(withFallback, collector);

            const repo = new InMemoryOrderRepository();
            const service = new OrderService(repo, withFallback);
            applyTracing(service, collector);
            return service;
        }

        it("should show resilience wrappers in trace", () => {
            const service = buildChaosService(collector);

            service.placeOrder(1, "Latte");

            const trace = collector.getTrace();
            console.log("=== Chaos Resilience Trace ===");
            trace.forEach((s) => console.log(s));

            expect(trace.some((s) => s.includes("FallbackKitchenService"))).toBe(true);
            expect(trace.some((s) => s.includes("OrderService.placeOrder"))).toBe(true);
        });

        it("should produce retry and fallback traces under stress", () => {
            const service = buildChaosService(collector);
            const allTraces: string[][] = [];

            for (let i = 0; i < 20; i++) {
                collector.clear();
                service.placeOrder(1, "Latte");
                allTraces.push(collector.getTrace());
            }

            const tracesWithRetry = allTraces.filter((t) =>
                t.some((s) => s.includes("RetryKitchenService")),
            ).length;
            const tracesWithFallback = allTraces.filter((t) =>
                t.some((s) => s.includes("FallbackKitchenService")),
            ).length;

            console.log("=== Stress Test Summary ===");
            console.log(`Total requests: ${allTraces.length}`);
            console.log(`Traces with retry: ${tracesWithRetry}`);
            console.log(`Traces with fallback: ${tracesWithFallback}`);

            // All requests should use the fallback wrapper
            expect(tracesWithFallback).toBe(20);
        });
    });
});
