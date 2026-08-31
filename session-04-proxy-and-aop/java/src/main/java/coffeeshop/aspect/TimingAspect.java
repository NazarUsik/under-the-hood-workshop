package coffeeshop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

// Second aspect in the chain: measures execution time.
// Because @Order(2) > @Order(1), this runs INSIDE the LoggingAspect.
// Call flow: Logging.before -> Timing.before -> real method -> Timing.after -> Logging.after
@Aspect
@Component
@Order(2)
public class TimingAspect {

    @Around("execution(public * coffeeshop..*Service.*(..))")
    public Object timeMethodCall(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long duration = System.currentTimeMillis() - start;
        System.out.println("[Timing] " + methodName + " took " + duration + "ms");
        return result;
    }
}
