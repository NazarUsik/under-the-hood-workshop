from functools import wraps


# Exercise 3: Validation decorator for order ID.
def validated_order_id(func):
    """Validates that order_id argument is positive."""
    @wraps(func)
    def wrapper(self, order_id, *args, **kwargs):
        if order_id <= 0:
            raise ValueError(f"Order ID must be positive, got: {order_id}")
        return func(self, order_id, *args, **kwargs)
    return wrapper
