package coffeeshop.resilience;

import coffeeshop.kitchen.KitchenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

// When chaos profile is active, this wraps the ChaosKitchenService with resilience layers.
// The proxy chain: Fallback -> Retry -> CircuitBreaker -> Timeout -> ChaosKitchenService
@Configuration
@Profile("chaos")
public class ResilienceConfig {

    @Bean
    @Primary
    public KitchenService resilientKitchenService(KitchenService chaosKitchenService) {
        // Full resilience stack (Exercise 5):
        KitchenService withTimeout = new TimeoutKitchenService(chaosKitchenService, 3000);
        KitchenService withCircuitBreaker = new CircuitBreakerKitchenService(withTimeout, 3, 10000);
        KitchenService withRetry = new RetryKitchenService(withCircuitBreaker, 3, 1000);
        return new FallbackKitchenService(withRetry);
    }
}
