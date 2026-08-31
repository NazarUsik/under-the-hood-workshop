import functools

from tracing.collector import TraceCollector


def traced(collector: TraceCollector):
    """Decorator factory that wraps a method to record before/after/error in the collector.
    Python's equivalent of an AOP aspect. Zero lines added to the original service class."""

    def decorator(method):
        @functools.wraps(method)
        def wrapper(*args, **kwargs):
            # args[0] is self; get class name from it
            class_name = type(args[0]).__name__ if args else "Unknown"
            step = f"{class_name}.{method.__name__}"

            collector.add(f"before:{step}")
            try:
                result = method(*args, **kwargs)
                collector.add(f"after:{step}")
                return result
            except Exception as e:
                collector.add(f"error:{step}:{e}")
                raise

        return wrapper

    return decorator


def apply_tracing(obj, collector: TraceCollector):
    """Apply tracing to all public methods of an object without modifying its class.
    This is Python's version of proxying: wrap methods at runtime."""
    for attr_name in dir(obj):
        if attr_name.startswith("_"):
            continue
        attr = getattr(obj, attr_name)
        if callable(attr):
            wrapped = _trace_method(obj, attr_name, collector)
            setattr(obj, attr_name, wrapped)
    return obj


def _trace_method(obj, method_name: str, collector: TraceCollector):
    original = getattr(obj, method_name)
    class_name = type(obj).__name__
    step = f"{class_name}.{method_name}"

    @functools.wraps(original)
    def wrapper(*args, **kwargs):
        collector.add(f"before:{step}")
        try:
            result = original(*args, **kwargs)
            collector.add(f"after:{step}")
            return result
        except Exception as e:
            collector.add(f"error:{step}:{e}")
            raise

    return wrapper
