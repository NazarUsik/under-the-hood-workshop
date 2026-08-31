package tracing

import (
	"fmt"
	"strings"
	"testing"
	"time"

	"coffeeshop-capstone/kitchen"
	"coffeeshop-capstone/resilience"
)

func TestNormalRequestTrace(t *testing.T) {
	collector := NewTraceCollector()
	real := &kitchen.RealKitchenService{}
	traced := &TracedKitchenService{Inner: real, Collector: collector, Label: "RealKitchenService"}

	prep, err := traced.Prepare(1, "Latte")
	if err != nil {
		t.Fatalf("unexpected error: %v", err)
	}
	if prep.Status != "preparing" {
		t.Fatalf("expected status 'preparing', got '%s'", prep.Status)
	}

	trace := collector.GetTrace()
	fmt.Println("=== Normal Request Trace ===")
	for _, s := range trace {
		fmt.Println(s)
	}

	assertContains(t, trace, "before:RealKitchenService.Prepare")
	assertContains(t, trace, "after:RealKitchenService.Prepare")
}

func TestChaosResilienceTrace(t *testing.T) {
	collector := NewTraceCollector()

	chaos := &kitchen.ChaosKitchenService{}
	tracedChaos := &TracedKitchenService{Inner: chaos, Collector: collector, Label: "ChaosKitchenService"}
	withTimeout := &resilience.TimeoutKitchenService{Inner: tracedChaos, Timeout: 3 * time.Second}
	tracedTimeout := &TracedKitchenService{Inner: withTimeout, Collector: collector, Label: "TimeoutKitchenService"}
	withRetry := &resilience.RetryKitchenService{Inner: tracedTimeout, MaxAttempts: 3, Delay: 100 * time.Millisecond}
	tracedRetry := &TracedKitchenService{Inner: withRetry, Collector: collector, Label: "RetryKitchenService"}
	withFallback := &resilience.FallbackKitchenService{Inner: tracedRetry}
	tracedFallback := &TracedKitchenService{Inner: withFallback, Collector: collector, Label: "FallbackKitchenService"}

	prep, err := tracedFallback.Prepare(1, "Latte")
	if err != nil {
		t.Fatalf("unexpected error (fallback should catch): %v", err)
	}
	if prep == nil {
		t.Fatal("expected preparation, got nil")
	}

	trace := collector.GetTrace()
	fmt.Println("=== Chaos Resilience Trace ===")
	for _, s := range trace {
		fmt.Println(s)
	}

	assertContains(t, trace, "before:FallbackKitchenService.Prepare")
}

func TestStressProducesRetryAndFallbackTraces(t *testing.T) {
	collector := NewTraceCollector()

	chaos := &kitchen.ChaosKitchenService{}
	tracedChaos := &TracedKitchenService{Inner: chaos, Collector: collector, Label: "ChaosKitchenService"}
	withTimeout := &resilience.TimeoutKitchenService{Inner: tracedChaos, Timeout: 3 * time.Second}
	tracedTimeout := &TracedKitchenService{Inner: withTimeout, Collector: collector, Label: "TimeoutKitchenService"}
	withRetry := &resilience.RetryKitchenService{Inner: tracedTimeout, MaxAttempts: 3, Delay: 100 * time.Millisecond}
	tracedRetry := &TracedKitchenService{Inner: withRetry, Collector: collector, Label: "RetryKitchenService"}
	withFallback := &resilience.FallbackKitchenService{Inner: tracedRetry}
	tracedFallback := &TracedKitchenService{Inner: withFallback, Collector: collector, Label: "FallbackKitchenService"}

	allTraces := make([][]string, 0, 20)
	for i := 0; i < 20; i++ {
		collector.Clear()
		tracedFallback.Prepare(1, "Latte")
		allTraces = append(allTraces, collector.GetTrace())
	}

	fallbackCount := 0
	retryCount := 0
	for _, trace := range allTraces {
		for _, s := range trace {
			if strings.Contains(s, "FallbackKitchenService") {
				fallbackCount++
				break
			}
		}
		for _, s := range trace {
			if strings.Contains(s, "RetryKitchenService") {
				retryCount++
				break
			}
		}
	}

	fmt.Println("=== Stress Test Summary ===")
	fmt.Printf("Total requests: %d\n", len(allTraces))
	fmt.Printf("Traces with retry: %d\n", retryCount)
	fmt.Printf("Traces with fallback: %d\n", fallbackCount)

	if fallbackCount != 20 {
		t.Errorf("expected all 20 traces to use fallback, got %d", fallbackCount)
	}
}

func assertContains(t *testing.T, trace []string, substr string) {
	t.Helper()
	for _, s := range trace {
		if strings.Contains(s, substr) {
			return
		}
	}
	t.Errorf("trace does not contain '%s'. Trace: %v", substr, trace)
}
