"""
A minimal DI container in ~30 lines of Python.
Uses inspect.signature() to read constructor type hints and resolve dependencies automatically.
"""

import inspect


class Container:

    def __init__(self):
        self._registry: dict[type, type] = {}  # interface -> implementation
        self._instances: dict[type, object] = {}  # singleton cache
        self._in_progress: set[type] = set()  # cycle detection

    def register(self, base_type: type, impl_type: type):
        """Step 1: Register a type mapping."""
        self._registry[base_type] = impl_type

    def resolve(self, requested_type: type) -> object:
        """Steps 2-4: Resolve a type by walking the dependency graph."""
        # Singleton: already created? Return it.
        if requested_type in self._instances:
            return self._instances[requested_type]

        # Step 3: Circular dependency detection
        if requested_type in self._in_progress:
            raise RuntimeError(f"Circular dependency detected: {requested_type.__name__}")
        self._in_progress.add(requested_type)

        # Look up the implementation class
        impl = self._registry.get(requested_type, requested_type)

        # Step 2: Read constructor params = the dependency graph edges
        params = inspect.signature(impl.__init__).parameters
        deps = {
            name: self.resolve(param.annotation)  # recursion = graph walk
            for name, param in params.items()
            if name != "self" and param.annotation != inspect.Parameter.empty
        }

        # Step 4: Instantiate with resolved dependencies
        instance = impl(**deps)
        self._instances[requested_type] = instance
        self._in_progress.discard(requested_type)
        return instance
