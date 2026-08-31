import time
from functools import wraps


# A Python decorator IS a proxy. It takes a function, returns a new function
# that wraps it. The caller can't tell the difference.


def logged(func):
    """Logs method name, arguments, and return value."""

    @wraps(func)
    def wrapper(*args, **kwargs):
        # 'args[0]' is 'self' for methods
        func_name = f"{args[0].__class__.__name__}.{func.__name__}" if args else func.__name__
        print(f"[Logging] >> {func_name}(args={args[1:]}, kwargs={kwargs})")
        result = func(*args, **kwargs)
        print(f"[Logging] << {func_name} returned={result}")
        return result

    return wrapper


def timed(func):
    """Measures and logs execution time."""

    @wraps(func)
    def wrapper(*args, **kwargs):
        func_name = func.__name__
        start = time.time()
        result = func(*args, **kwargs)
        duration_ms = (time.time() - start) * 1000
        print(f"[Timing] {func_name} took {duration_ms:.0f}ms")
        return result

    return wrapper


def audited(func):
    """Logs operation start and completion/failure."""

    @wraps(func)
    def wrapper(*args, **kwargs):
        func_name = func.__name__
        print(f"[Audit] operation started: {func_name}")
        try:
            result = func(*args, **kwargs)
            print(f"[Audit] operation completed: {func_name}")
            return result
        except Exception as e:
            print(f"[Audit] operation FAILED: {func_name} error={e}")
            raise

    return wrapper
