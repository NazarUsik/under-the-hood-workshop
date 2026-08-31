package coffeeshop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

// Exercise 4: Aspect that only targets methods annotated with @Timed.
@Aspect
@Component
public class TimedAspect {

    @Around("@annotation(coffeeshop.aspect.Timed)")
    public Object timeAnnotatedMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        String method = joinPoint.getSignature().toShortString();
        long start = System.currentTimeMillis();

        Object result = joinPoint.proceed();

        long duration = System.currentTimeMillis() - start;
        System.out.println("[@Timed] " + method + " took " + duration + "ms");
        return result;
    }
}
