package coffeeshop.middleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

// HandlerInterceptor: runs inside Spring MVC, after filters but before the controller.
// preHandle runs before your handler. postHandle runs after (but before the response is sent).
// This is where you hook into Spring's request lifecycle specifically.
@Component
public class TimingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute("startTime", System.currentTimeMillis());
        System.out.println("[Interceptor] preHandle: " + request.getMethod() + " " + request.getRequestURI());
        return true; // returning false would short-circuit: handler never runs
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        long start = (long) request.getAttribute("startTime");
        long duration = System.currentTimeMillis() - start;
        System.out.println("[Interceptor] afterCompletion: " + duration + "ms"
            + (ex != null ? " (error: " + ex.getMessage() + ")" : ""));
    }
}
