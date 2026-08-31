import time

from kitchen.service import KitchenService, Preparation


class RateLimiterKitchenService(KitchenService):
    """Exercise: Rate limiter wrapper. Rejects if more than max_requests in window_seconds."""

    def __init__(self, inner: KitchenService, max_requests: int = 10, window_seconds: float = 60.0):
        self.inner = inner
        self.max_requests = max_requests
        self.window_seconds = window_seconds
        self._request_count = 0
        self._window_start = time.time()

    def prepare(self, order_id: int, drink: str) -> Preparation:
        now = time.time()
        if now - self._window_start > self.window_seconds:
            self._window_start = now
            self._request_count = 0

        self._request_count += 1
        if self._request_count > self.max_requests:
            raise RuntimeError(
                f"Rate limit exceeded: max {self.max_requests} requests per {self.window_seconds}s"
            )

        print(f"[RateLimiter] Request {self._request_count}/{self.max_requests}")
        return self.inner.prepare(order_id, drink)
