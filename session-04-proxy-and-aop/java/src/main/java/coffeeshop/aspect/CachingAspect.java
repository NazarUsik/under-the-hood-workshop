package coffeeshop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// Exercise 2: Caching aspect. Caches the result of MenuService.listItems().
// First call hits the repository, subsequent calls return cached result.
@Aspect
@Component
public class CachingAspect {

    private final Map<String, Object> cache = new ConcurrentHashMap<>();

    @Around("execution(* coffeeshop.menu.MenuService.listItems(..))")
    public Object cacheResult(ProceedingJoinPoint joinPoint) throws Throwable {
        String key = joinPoint.getSignature().toShortString();

        if (cache.containsKey(key)) {
            System.out.println("[Cache] HIT for " + key);
            return cache.get(key);
        }

        System.out.println("[Cache] MISS for " + key);
        Object result = joinPoint.proceed();
        cache.put(key, result);
        return result;
    }
}
