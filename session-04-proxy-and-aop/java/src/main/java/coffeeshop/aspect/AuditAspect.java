package coffeeshop.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

// This aspect uses separate @Before, @AfterReturning, and @AfterThrowing advice
// instead of @Around. It shows the different advice types available in Spring AOP.
@Aspect
@Component
public class AuditAspect {

    // Runs before any method in OrderService
    @Before("execution(* coffeeshop.order.OrderService.*(..))")
    public void auditBefore(JoinPoint joinPoint) {
        System.out.println("[Audit] operation started: " + joinPoint.getSignature().getName());
    }

    // Runs after a successful return from any method in OrderService
    @AfterReturning(pointcut = "execution(* coffeeshop.order.OrderService.*(..))", returning = "result")
    public void auditAfterReturning(JoinPoint joinPoint, Object result) {
        System.out.println("[Audit] operation completed: " + joinPoint.getSignature().getName());
    }

    // Runs if the method throws an exception
    @AfterThrowing(pointcut = "execution(* coffeeshop.order.OrderService.*(..))", throwing = "ex")
    public void auditAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        System.out.println("[Audit] operation FAILED: " + joinPoint.getSignature().getName()
            + " error=" + ex.getMessage());
    }
}
