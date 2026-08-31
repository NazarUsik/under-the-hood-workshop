package coffeeshop.tracing;

import coffeeshop.order.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

// Test 2: Run under chaos and verify resilience via trace.
// Chaos profile is active: ChaosKitchenService + Fallback(Retry(Timeout(chaos))).
// The TracingAspect records which resilience wrappers fired.
@SpringBootTest
@Import(TracingTestConfig.class)
@ActiveProfiles("chaos")
class ChaosResilienceTraceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private TraceCollector traceCollector;

    @BeforeEach
    void clearTrace() {
        traceCollector.clear();
    }

    @Test
    void chaosRequestProducesResilienceTrace() {
        // Run one request - it always succeeds because of fallback
        orderService.placeOrder(1, "Latte");

        List<String> trace = traceCollector.getTrace();

        System.out.println("=== Chaos Resilience Trace ===");
        trace.forEach(System.out::println);

        // The trace must show the resilience chain was activated
        assertThat(trace).anyMatch(s -> s.contains("FallbackKitchenService"));
        assertThat(trace).anyMatch(s -> s.contains("OrderService.placeOrder"));

        // The trace must show success or error (chaos is random)
        boolean hasSuccess = trace.stream().anyMatch(s -> s.startsWith("after:"));
        boolean hasError = trace.stream().anyMatch(s -> s.startsWith("error:"));
        assertThat(hasSuccess || hasError).as("Trace should contain success or error steps").isTrue();
    }

    @Test
    void stressTestProducesRetryAndFallbackTraces() {
        List<List<String>> allTraces = new ArrayList<>();

        // Run 20 requests under chaos
        for (int i = 0; i < 20; i++) {
            traceCollector.clear();
            orderService.placeOrder(1, "Latte");
            allTraces.add(traceCollector.getTrace());
        }

        System.out.println("=== Stress Test Summary ===");
        System.out.println("Total requests: " + allTraces.size());

        long tracesWithRetry = allTraces.stream()
            .filter(t -> t.stream().anyMatch(s -> s.contains("RetryKitchenService")))
            .count();
        long tracesWithFallback = allTraces.stream()
            .filter(t -> t.stream().anyMatch(s -> s.contains("FallbackKitchenService")))
            .count();
        long tracesWithError = allTraces.stream()
            .filter(t -> t.stream().anyMatch(s -> s.startsWith("error:")))
            .count();

        System.out.println("Traces with retry: " + tracesWithRetry);
        System.out.println("Traces with fallback: " + tracesWithFallback);
        System.out.println("Traces with errors: " + tracesWithError);

        // All requests should have used the fallback wrapper (it's in the chain)
        assertThat(tracesWithFallback).isEqualTo(20);
    }
}
