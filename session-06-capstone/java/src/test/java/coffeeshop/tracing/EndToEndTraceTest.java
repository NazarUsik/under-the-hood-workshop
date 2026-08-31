package coffeeshop.tracing;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Test 3: Full end-to-end trace test.
// Sends an HTTP request via MockMvc, then reads the trace to verify
// the complete journey from controller through service to kitchen.
@SpringBootTest
@AutoConfigureMockMvc
@Import(TracingTestConfig.class)
class EndToEndTraceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TraceCollector traceCollector;

    @Test
    void fullRequestProducesCompleteTrace() throws Exception {
        traceCollector.clear();

        mockMvc.perform(post("/orders/1/prepare"))
            .andExpect(status().isOk());

        List<String> trace = traceCollector.getTrace();

        System.out.println("=== End-to-End Trace ===");
        trace.forEach(System.out::println);

        // Verify the trace captured the full request lifecycle
        assertThat(trace).anyMatch(s -> s.contains("OrderService.placeOrder"));
        assertThat(trace).anyMatch(s -> s.contains("KitchenService.prepare")
            || s.contains("RealKitchenService.prepare"));

        // Verify the trace has proper before/after pairing
        long beforeCount = trace.stream().filter(s -> s.startsWith("before:")).count();
        long afterCount = trace.stream().filter(s -> s.startsWith("after:")).count();
        assertThat(beforeCount).as("Every before: should have a matching after:").isEqualTo(afterCount);
    }
}
