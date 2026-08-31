import time

from kitchen.service import KitchenService, Preparation


class RetryKitchenService(KitchenService):
    """Exercise 3: Retries up to max_attempts times on failure."""

    def __init__(self, inner: KitchenService, max_attempts: int = 3, delay_seconds: float = 1.0):
        self.inner = inner
        self.max_attempts = max_attempts
        self.delay_seconds = delay_seconds

    def prepare(self, order_id: int, drink: str) -> Preparation:
        last_error = None
        for attempt in range(1, self.max_attempts + 1):
            try:
                result = self.inner.prepare(order_id, drink)
                if attempt > 1:
                    print(f"[Retry] Succeeded on attempt {attempt} for order #{order_id}")
                return result
            except Exception as e:
                last_error = e
                print(f"[Retry] Attempt {attempt}/{self.max_attempts} failed for order #{order_id}: {e}")
                if attempt < self.max_attempts:
                    time.sleep(self.delay_seconds)
        raise RuntimeError(f"All {self.max_attempts} attempts failed for order #{order_id}") from last_error
