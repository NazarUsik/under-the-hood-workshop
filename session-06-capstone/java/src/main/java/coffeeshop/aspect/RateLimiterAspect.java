package coffeeshop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

// Exercise 4: Rate limiter aspect.
// Rejects requests if more than maxRequests arrive within windowMs.
// Fires before the service call (outside logging and timing).
@Aspect
@Component
@Order(0)
public class RateLimiterAspect {

    private static final int MAX_REQUESTS = 10;
    private static final long WINDOW_MS = 60_000;

    private final AtomicInteger requestCount = new AtomicInteger(0);
    private final AtomicLong windowStart = new AtomicLong(System.currentTimeMillis());

    @Around("execution(public * coffeeshop.order.OrderService.*(..))")
    public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        long now = System.currentTimeMillis();

        if (now - windowStart.get() > WINDOW_MS) {
            windowStart.set(now);
            requestCount.set(0);
        }

        if (requestCount.incrementAndGet() > MAX_REQUESTS) {
            throw new RuntimeException("Rate limit exceeded: max " + MAX_REQUESTS + " requests per minute");
        }

        System.out.println("[RateLimiter] Request " + requestCount.get() + "/" + MAX_REQUESTS);
        return joinPoint.proceed();
    }
}
