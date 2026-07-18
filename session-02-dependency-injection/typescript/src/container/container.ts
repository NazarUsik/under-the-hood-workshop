/**
 * A minimal DI container in ~30 lines of TypeScript.
 * Uses reflect-metadata to read constructor parameter types and resolve dependencies.
 * This is the same mechanism NestJS uses under the hood.
 */
import "reflect-metadata";

export class Container {
    private registry = new Map<Function, Function>();       // interface -> implementation
    private instances = new Map<Function, unknown>();        // singleton cache
    private inProgress = new Set<Function>();                // cycle detection

    // Step 1: Register a type mapping
    register(token: Function, impl: Function): void {
        this.registry.set(token, impl);
    }

    // Steps 2-4: Resolve a type by walking the dependency graph
    resolve<T>(token: Function): T {
        // Singleton: already created? Return it.
        if (this.instances.has(token)) {
            return this.instances.get(token) as T;
        }

        // Step 3: Circular dependency detection
        if (this.inProgress.has(token)) {
            throw new Error(`Circular dependency detected: ${token.name}`);
        }
        this.inProgress.add(token);

        const impl = this.registry.get(token) ?? token;

        // Step 2: Read constructor param types via reflect-metadata (the graph edges)
        const paramTypes: Function[] = Reflect.getMetadata("design:paramtypes", impl) ?? [];

        // Recursively resolve all constructor parameters
        const deps = paramTypes.map((dep) => this.resolve(dep));

        // Step 4: Instantiate with resolved dependencies
        const instance = new (impl as any)(...deps) as T;
        this.instances.set(token, instance);
        this.inProgress.delete(token);
        return instance;
    }
}
