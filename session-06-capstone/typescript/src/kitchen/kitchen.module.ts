import {Module} from "@nestjs/common";
import {ChaosKitchenService, KitchenService, RealKitchenService} from "./kitchen.service";
import {FallbackKitchenService} from "../resilience/fallback.service";
import {RetryKitchenService} from "../resilience/retry.service";
import {TimeoutKitchenService} from "../resilience/timeout.service";

const chaosMode = process.env.CHAOS_MODE === "true";

// DI swap: in chaos mode, wire Fallback(Retry(Timeout(Chaos)))
const kitchenProvider = chaosMode
    ? {
        provide: KitchenService,
        useFactory: () => {
            const chaos = new ChaosKitchenService();
            const withTimeout = new TimeoutKitchenService(chaos, 3000);
            const withRetry = new RetryKitchenService(withTimeout, 3, 500);
            return new FallbackKitchenService(withRetry);
        },
    }
    : {provide: KitchenService, useClass: RealKitchenService};

@Module({
    providers: [kitchenProvider],
    exports: [KitchenService],
})
export class KitchenModule {
}
