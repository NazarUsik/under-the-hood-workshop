package coffeeshop.middleware;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Register the HandlerInterceptor. Filters are auto-registered via @Component,
// but interceptors need explicit registration through WebMvcConfigurer.
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final TimingInterceptor timingInterceptor;
    private final AuthInterceptor authInterceptor;

    public WebConfig(TimingInterceptor timingInterceptor, AuthInterceptor authInterceptor) {
        this.timingInterceptor = timingInterceptor;
        this.authInterceptor = authInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor).addPathPatterns("/**");
        registry.addInterceptor(timingInterceptor).addPathPatterns("/**");
    }
}
