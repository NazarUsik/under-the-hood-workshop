import {TraceCollector} from "./trace-collector";

// Wraps all public methods of an object with tracing.
// TypeScript/NestJS equivalent of AOP: runtime method proxying.
// Zero lines added to any existing service class.
export function applyTracing<T extends object>(obj: T, collector: TraceCollector): T {
    const className = obj.constructor.name;
    const proto = Object.getPrototypeOf(obj);

    for (const key of Object.getOwnPropertyNames(proto)) {
        if (key === "constructor") continue;
        const descriptor = Object.getOwnPropertyDescriptor(proto, key);
        if (!descriptor || typeof descriptor.value !== "function") continue;

        const originalMethod = descriptor.value;
        const step = `${className}.${key}`;

        (obj as any)[key] = function (...args: any[]) {
            collector.add(`before:${step}`);
            try {
                const result = originalMethod.apply(this, args);
                collector.add(`after:${step}`);
                return result;
            } catch (e) {
                collector.add(`error:${step}:${(e as Error).message}`);
                throw e;
            }
        };
    }

    return obj;
}
