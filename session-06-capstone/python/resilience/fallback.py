from kitchen.service import KitchenService, Preparation


class FallbackKitchenService(KitchenService):
    def __init__(self, inner: KitchenService):
        self.inner = inner

    def prepare(self, order_id: int, drink: str) -> Preparation:
        try:
            return self.inner.prepare(order_id, drink)
        except Exception as e:
            print(f"[Fallback] Kitchen failed: {e} -- returning queued for order #{order_id}")
            return Preparation(order_id=order_id, drink=drink, status="queued")
