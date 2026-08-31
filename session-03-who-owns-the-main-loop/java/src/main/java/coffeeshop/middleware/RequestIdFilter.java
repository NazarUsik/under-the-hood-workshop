package coffeeshop.middleware;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.UUID;

// Exercise 2: Request ID filter. Generates a UUID per request and adds it to the response header.
@Component
@Order(0)
public class RequestIdFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String requestId = UUID.randomUUID().toString();
        req.setAttribute("requestId", requestId);
        ((HttpServletResponse) res).setHeader("X-Request-Id", requestId);
        System.out.println("[RequestId] " + requestId);
        chain.doFilter(req, res);
    }
}
