import concurrent.futures

from kitchen.service import KitchenService, Preparation


class TimeoutKitchenService(KitchenService):
    def __init__(self, inner: KitchenService, timeout_seconds: float):
        self.inner = inner
        self.timeout_seconds = timeout_seconds

    def prepare(self, order_id: int, drink: str) -> Preparation:
        with concurrent.futures.ThreadPoolExecutor() as executor:
            future = executor.submit(self.inner.prepare, order_id, drink)
            try:
                return future.result(timeout=self.timeout_seconds)
            except concurrent.futures.TimeoutError:
                raise RuntimeError(
                    f"Kitchen timed out after {self.timeout_seconds}s for order #{order_id}"
                )
