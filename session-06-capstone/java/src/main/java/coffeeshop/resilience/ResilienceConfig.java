package coffeeshop.resilience;

import coffeeshop.kitchen.KitchenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

// When chaos profile is active, wraps ChaosKitchenService with resilience layers.
// Chain: Fallback -> Retry -> Timeout -> ChaosKitchenService
@Configuration
@Profile("chaos")
public class ResilienceConfig {

    @Bean
    @Primary
    public KitchenService resilientKitchenService(KitchenService chaosKitchenService) {
        KitchenService withTimeout = new TimeoutKitchenService(chaosKitchenService, 3000);
        KitchenService withRetry = new RetryKitchenService(withTimeout, 3, 500);
        return new FallbackKitchenService(withRetry);
    }
}
