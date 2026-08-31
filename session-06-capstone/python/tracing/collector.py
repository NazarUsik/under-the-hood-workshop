import threading


class TraceCollector:
    """Thread-safe list that records every step a request takes through the system."""

    def __init__(self):
        self._trace: list[str] = []
        self._lock = threading.Lock()

    def add(self, step: str):
        with self._lock:
            self._trace.append(step)

    def get_trace(self) -> list[str]:
        with self._lock:
            return list(self._trace)

    def clear(self):
        with self._lock:
            self._trace.clear()
