package coffeeshop.middleware;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

// Exercise 3: Auth interceptor. Checks for Authorization header.
// Returns false from preHandle to short-circuit: the controller never runs.
@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String auth = request.getHeader("Authorization");
        if (auth == null || auth.isBlank()) {
            System.out.println("[Auth] Missing Authorization header, returning 401");
            response.setStatus(401);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Unauthorized\"}");
            return false; // short-circuit: handler does NOT run
        }
        System.out.println("[Auth] Authorized: " + auth);
        return true;
    }
}
