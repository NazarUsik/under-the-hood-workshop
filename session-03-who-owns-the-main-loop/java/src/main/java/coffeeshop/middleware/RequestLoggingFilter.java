package coffeeshop.middleware;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

// Servlet Filter: the outermost middleware layer.
// This runs for every HTTP request, before Spring MVC even sees it.
// chain.doFilter() is the "call next" that passes control to the next filter or the DispatcherServlet.
@Component
@Order(1)
public class RequestLoggingFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        var httpReq = (HttpServletRequest) req;
        var httpRes = (HttpServletResponse) res;
        long start = System.currentTimeMillis();

        System.out.println("[Filter] >> " + httpReq.getMethod() + " " + httpReq.getRequestURI());

        chain.doFilter(req, res);

        long duration = System.currentTimeMillis() - start;
        System.out.println("[Filter] << " + httpRes.getStatus() + " (" + duration + "ms)");
    }
}
