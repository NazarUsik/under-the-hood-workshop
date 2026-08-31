package coffeeshop.tracing;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;

// This aspect intercepts ALL service and resilience method calls and records them
// in the TraceCollector. It exists in main code but is only activated via test config.
//
// Zero lines added to any existing class. This is the X-ray machine.
@Aspect
@Order(0) // runs outside all other aspects so it captures everything
public class TracingAspect {

    private final TraceCollector collector;

    public TracingAspect(TraceCollector collector) {
        this.collector = collector;
    }

    @Around("execution(public * coffeeshop..*Service.*(..)) || execution(public * coffeeshop..*KitchenService.*(..))")
    public Object trace(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String step = className + "." + methodName;

        collector.add("before:" + step);
        try {
            Object result = joinPoint.proceed();
            collector.add("after:" + step);
            return result;
        } catch (Throwable t) {
            collector.add("error:" + step + ":" + t.getMessage());
            throw t;
        }
    }
}
