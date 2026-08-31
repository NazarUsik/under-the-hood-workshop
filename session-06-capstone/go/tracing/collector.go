package tracing

import "sync"

// TraceCollector records every step a request takes through the system.
// Thread-safe via mutex. Only used in tests.
type TraceCollector struct {
	mu    sync.Mutex
	trace []string
}

func NewTraceCollector() *TraceCollector {
	return &TraceCollector{}
}

func (c *TraceCollector) Add(step string) {
	c.mu.Lock()
	defer c.mu.Unlock()
	c.trace = append(c.trace, step)
}

func (c *TraceCollector) GetTrace() []string {
	c.mu.Lock()
	defer c.mu.Unlock()
	result := make([]string, len(c.trace))
	copy(result, c.trace)
	return result
}

func (c *TraceCollector) Clear() {
	c.mu.Lock()
	defer c.mu.Unlock()
	c.trace = nil
}
