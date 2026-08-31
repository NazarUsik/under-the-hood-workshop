package coffeeshop.resilience;

import coffeeshop.kitchen.KitchenService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

// When chaos profile is active, this wraps the ChaosKitchenService with resilience layers.
// The proxy chain: Fallback -> Timeout -> ChaosKitchenService
@Configuration
@Profile("chaos")
public class ResilienceConfig {

    @Bean
    @Primary
    public KitchenService resilientKitchenService(KitchenService chaosKitchenService) {
        // Wrap chaos service with timeout (3 seconds), then fallback
        KitchenService withTimeout = new TimeoutKitchenService(chaosKitchenService, 3000);
        return new FallbackKitchenService(withTimeout);
    }
}
