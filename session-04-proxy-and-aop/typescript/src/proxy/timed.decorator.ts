// Exercise 3: Method-level @Timed() decorator (not interceptor).
// Works at the service level, not the controller/request level.
export function Timed() {
    return function (target: any, key: string, descriptor: PropertyDescriptor) {
        const original = descriptor.value;
        descriptor.value = function (...args: any[]) {
            const start = Date.now();
            const result = original.apply(this, args);
            const duration = Date.now() - start;
            console.log(`[@Timed] ${key} took ${duration}ms`);
            return result;
        };
    };
}
