from functools import wraps


# Exercise 2: Caching decorator.
def cached(func):
    """Caches the result of a function call. Subsequent calls return cached value."""
    cache = {}

    @wraps(func)
    def wrapper(*args, **kwargs):
        key = func.__name__
        if key in cache:
            print(f"[Cache] HIT for {key}")
            return cache[key]
        print(f"[Cache] MISS for {key}")
        result = func(*args, **kwargs)
        cache[key] = result
        return result
    return wrapper
