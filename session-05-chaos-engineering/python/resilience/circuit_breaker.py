import time

from kitchen.service import KitchenService, Preparation


class CircuitBreakerKitchenService(KitchenService):
    """Exercise 4: Opens after failure_threshold consecutive failures, stays open for open_duration_seconds."""

    def __init__(self, inner: KitchenService, failure_threshold: int = 3, open_duration_seconds: float = 10.0):
        self.inner = inner
        self.failure_threshold = failure_threshold
        self.open_duration_seconds = open_duration_seconds
        self._consecutive_failures = 0
        self._opened_at = 0.0
        self._is_open = False

    def prepare(self, order_id: int, drink: str) -> Preparation:
        if self._is_open:
            if time.time() - self._opened_at > self.open_duration_seconds:
                print(f"[CircuitBreaker] Half-open: trying one request for order #{order_id}")
                self._is_open = False
                self._consecutive_failures = 0
            else:
                raise RuntimeError(f"Circuit breaker is OPEN. Rejecting order #{order_id}")

        try:
            result = self.inner.prepare(order_id, drink)
            self._consecutive_failures = 0
            return result
        except Exception as e:
            self._consecutive_failures += 1
            if self._consecutive_failures >= self.failure_threshold:
                self._is_open = True
                self._opened_at = time.time()
                print(f"[CircuitBreaker] OPENED after {self._consecutive_failures} consecutive failures")
            raise
