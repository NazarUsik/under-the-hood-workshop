package coffeeshop.tracing;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

// Test-only configuration. Registers the TracingAspect and TraceCollector beans.
// Production code has neither. DI makes this possible: register in tests, skip in prod.
@TestConfiguration
public class TracingTestConfig {

    @Bean
    public TraceCollector traceCollector() {
        return new TraceCollector();
    }

    @Bean
    public TracingAspect tracingAspect(TraceCollector collector) {
        return new TracingAspect(collector);
    }
}
