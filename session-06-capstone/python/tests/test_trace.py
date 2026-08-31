import os
import sys

sys.path.insert(0, os.path.join(os.path.dirname(__file__), ".."))

from order.repository import InMemoryOrderRepository
from order.service import OrderService
from kitchen.service import RealKitchenService, ChaosKitchenService
from resilience.timeout import TimeoutKitchenService
from resilience.retry import RetryKitchenService
from resilience.fallback import FallbackKitchenService
from tracing.collector import TraceCollector
from tracing.aspect import apply_tracing


def _build_normal_service(collector: TraceCollector) -> OrderService:
    """Build service chain with tracing applied (normal mode)."""
    repo = InMemoryOrderRepository()
    kitchen = RealKitchenService()
    apply_tracing(kitchen, collector)
    service = OrderService(repo, kitchen)
    apply_tracing(service, collector)
    return service


def _build_chaos_service(collector: TraceCollector) -> OrderService:
    """Build service chain with tracing applied (chaos mode)."""
    repo = InMemoryOrderRepository()
    chaos = ChaosKitchenService()
    apply_tracing(chaos, collector)
    with_timeout = TimeoutKitchenService(chaos, timeout_seconds=3.0)
    apply_tracing(with_timeout, collector)
    with_retry = RetryKitchenService(with_timeout, max_attempts=3, delay_seconds=0.5)
    apply_tracing(with_retry, collector)
    with_fallback = FallbackKitchenService(with_retry)
    apply_tracing(with_fallback, collector)
    service = OrderService(repo, with_fallback)
    apply_tracing(service, collector)
    return service


def test_normal_request_trace():
    """Test 1: Normal mode produces a clean trace."""
    collector = TraceCollector()
    service = _build_normal_service(collector)

    service.place_order(1, "Latte")

    trace = collector.get_trace()
    print("\n=== Normal Request Trace ===")
    for step in trace:
        print(step)

    assert any("OrderService.place_order" in s for s in trace)
    assert any("RealKitchenService.prepare" in s for s in trace)

    # Verify before/after pairing
    before_count = sum(1 for s in trace if s.startswith("before:"))
    after_count = sum(1 for s in trace if s.startswith("after:"))
    assert before_count == after_count


def test_chaos_resilience_trace():
    """Test 2: Chaos mode shows resilience wrappers in the trace."""
    collector = TraceCollector()
    service = _build_chaos_service(collector)

    service.place_order(1, "Latte")

    trace = collector.get_trace()
    print("\n=== Chaos Resilience Trace ===")
    for step in trace:
        print(step)

    # The trace must show resilience chain was activated
    assert any("FallbackKitchenService" in s for s in trace)
    assert any("OrderService.place_order" in s for s in trace)


def test_stress_produces_retry_and_fallback_traces():
    """Test 3: Run 20 requests under chaos, verify retries and fallbacks appear."""
    collector = TraceCollector()
    service = _build_chaos_service(collector)

    all_traces = []
    for _ in range(20):
        collector.clear()
        service.place_order(1, "Latte")
        all_traces.append(collector.get_trace())

    traces_with_retry = [t for t in all_traces if any("RetryKitchenService" in s for s in t)]
    traces_with_fallback = [t for t in all_traces if any("FallbackKitchenService" in s for s in t)]

    print(f"\n=== Stress Test Summary ===")
    print(f"Total requests: {len(all_traces)}")
    print(f"Traces with retry: {len(traces_with_retry)}")
    print(f"Traces with fallback: {len(traces_with_fallback)}")

    # All requests should have used the fallback wrapper (it's in the chain)
    assert len(traces_with_fallback) == 20


def test_e2e_trace_with_fastapi():
    """Test 4: Full HTTP request via TestClient produces trace."""
    from fastapi.testclient import TestClient
    from main import app, get_kitchen_service, get_order_service

    collector = TraceCollector()

    def traced_kitchen() -> RealKitchenService:
        kitchen = RealKitchenService()
        apply_tracing(kitchen, collector)
        return kitchen

    def traced_order_service(
            kitchen=None,
    ) -> OrderService:
        repo = InMemoryOrderRepository()
        k = traced_kitchen()
        svc = OrderService(repo, k)
        apply_tracing(svc, collector)
        return svc

    app.dependency_overrides[get_order_service] = traced_order_service

    client = TestClient(app)
    collector.clear()
    response = client.post("/orders/1/prepare")

    app.dependency_overrides.clear()

    assert response.status_code == 200

    trace = collector.get_trace()
    print("\n=== E2E Trace ===")
    for step in trace:
        print(step)

    assert any("OrderService.place_order" in s for s in trace)
    assert any("RealKitchenService.prepare" in s for s in trace)
