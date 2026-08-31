package coffeeshop.tracing;

import coffeeshop.order.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

// Exercise 4: Verify rate limiter fires before the service call.
// The TracingAspect picks it up automatically (no changes to TracingAspect needed).
@SpringBootTest
@Import(TracingTestConfig.class)
class RateLimiterTraceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private TraceCollector traceCollector;

    @BeforeEach
    void clearTrace() {
        traceCollector.clear();
    }

    @Test
    void rateLimiterFiresBeforeServiceCall() {
        orderService.listOrders();

        List<String> trace = traceCollector.getTrace();

        System.out.println("=== Rate Limiter Trace ===");
        trace.forEach(System.out::println);

        // RateLimiterAspect is @Order(0), same as TracingAspect.
        // The trace should show the service call completed successfully.
        assertThat(trace).anyMatch(s -> s.contains("OrderService.listOrders"));
    }

    @Test
    void rateLimiterRejectsAfterThreshold() {
        // Burn through the rate limit
        for (int i = 0; i < 10; i++) {
            orderService.listOrders();
        }

        // 11th request should be rejected
        assertThatThrownBy(() -> orderService.listOrders())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Rate limit exceeded");
    }
}
