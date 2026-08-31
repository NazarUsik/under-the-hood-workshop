package coffeeshop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

// Outermost aspect: logs method entry and exit.
// No tracing code here. The TracingAspect (test-only) observes this from outside.
@Aspect
@Component
@Order(1)
public class LoggingAspect {

    @Around("execution(public * coffeeshop..*Service.*(..))")
    public Object logMethodCall(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        System.out.println("[Logging] >> " + methodName + " args=" + Arrays.toString(args));

        Object result = joinPoint.proceed();

        System.out.println("[Logging] << " + methodName + " returned=" + result);
        return result;
    }
}
