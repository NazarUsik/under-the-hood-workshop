package coffeeshop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;

// Aspect: the cross-cutting behavior (logging).
// Pointcut: all public methods in *Service classes.
// Advice: @Around - runs before AND after the method.
//
// Spring creates a CGLIB proxy for each service bean.
// When the controller calls orderService.listOrders(), it actually calls
// the proxy, which runs this aspect, which calls the real method via proceed().
@Aspect
@Component
@Order(1) // outermost aspect: runs first
public class LoggingAspect {

    // Pointcut expression: match all public methods in any class ending with "Service"
    // in the coffeeshop package (and subpackages).
    @Around("execution(public * coffeeshop..*Service.*(..))")
    public Object logMethodCall(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        Object[] args = joinPoint.getArgs();

        System.out.println("[Logging] >> " + methodName + " args=" + Arrays.toString(args));

        Object result = joinPoint.proceed(); // call the real method (or the next proxy)

        System.out.println("[Logging] << " + methodName + " returned=" + result);
        return result;
    }
}
