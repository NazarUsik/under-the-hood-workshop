package coffeeshop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

// Exercise 3: Validation aspect. Checks that order ID is > 0.
// The service code doesn't change at all.
@Aspect
@Component
public class ValidationAspect {

    @Around("execution(* coffeeshop.order.OrderService.findOrder(int)) && args(id)")
    public Object validateOrderId(ProceedingJoinPoint joinPoint, int id) throws Throwable {
        if (id <= 0) {
            throw new IllegalArgumentException("Order ID must be positive, got: " + id);
        }
        return joinPoint.proceed();
    }
}
