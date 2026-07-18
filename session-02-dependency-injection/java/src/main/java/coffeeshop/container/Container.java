package coffeeshop.container;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A minimal DI container in ~40 lines.
 * Does the same four steps as Spring: register, resolve dependencies, detect cycles, instantiate.
 * No annotations, no classpath scanning - just reflection and a type registry.
 */
public class Container {

    private final Map<Class<?>, Class<?>> registry = new HashMap<>();
    private final Map<Class<?>, Object> instances = new HashMap<>();
    private final Set<Class<?>> inProgress = new HashSet<>();

    // Step 1: Register a type mapping (interface -> implementation)
    public <T> void register(Class<T> type, Class<? extends T> impl) {
        registry.put(type, impl);
    }

    // Step 2-4: Resolve a type, building the dependency graph on demand
    @SuppressWarnings("unchecked")
    public <T> T resolve(Class<T> type) {
        // Already created? Return the singleton.
        if (instances.containsKey(type)) {
            return (T) instances.get(type);
        }

        // Step 3: Circular dependency detection
        if (inProgress.contains(type)) {
            throw new RuntimeException("Circular dependency detected for: " + type.getName());
        }
        inProgress.add(type);

        Class<?> impl = registry.getOrDefault(type, type);

        // Find the first constructor
        var constructor = impl.getDeclaredConstructors()[0];

        // Recursively resolve all constructor parameters (Step 2: graph walk)
        var params = Arrays.stream(constructor.getParameterTypes())
                .map(this::resolve)
                .toArray();

        try {
            // Step 4: Instantiate with resolved dependencies
            T instance = (T) constructor.newInstance(params);
            instances.put(type, instance);
            inProgress.remove(type);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create: " + type.getName(), e);
        }
    }
}
