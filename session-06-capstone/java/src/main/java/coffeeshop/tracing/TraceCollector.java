package coffeeshop.tracing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Thread-safe list that records every step a request takes through the system.
// This class exists in main code but is only registered as a bean in test configuration.
// Production has no TraceCollector bean and no TracingAspect.
public class TraceCollector {

    private final List<String> trace = Collections.synchronizedList(new ArrayList<>());

    public void add(String step) {
        trace.add(step);
    }

    public List<String> getTrace() {
        return List.copyOf(trace);
    }

    public void clear() {
        trace.clear();
    }
}
