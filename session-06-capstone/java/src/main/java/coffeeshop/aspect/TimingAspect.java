package coffeeshop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

// Runs inside LoggingAspect: Logging.before -> Timing.before -> method -> Timing.after -> Logging.after
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
