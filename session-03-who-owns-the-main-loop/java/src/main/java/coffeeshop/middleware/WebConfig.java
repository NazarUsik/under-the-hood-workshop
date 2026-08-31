package coffeeshop.middleware;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Register the HandlerInterceptor. Filters are auto-registered via @Component,
// but interceptors need explicit registration through WebMvcConfigurer.
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final TimingInterceptor timingInterceptor;

    public WebConfig(TimingInterceptor timingInterceptor) {
        this.timingInterceptor = timingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(timingInterceptor).addPathPatterns("/**");
    }
}
