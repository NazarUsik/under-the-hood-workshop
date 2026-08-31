import {Module} from "@nestjs/common";
import {ChaosKitchenService, KitchenService, RealKitchenService} from "./kitchen.service";
import {FallbackKitchenService} from "../resilience/fallback.service";
import {TimeoutKitchenService} from "../resilience/timeout.service";

const chaosMode = process.env.CHAOS_MODE === "true";

// DI swap: in chaos mode, wire Fallback(Timeout(Chaos))
// In normal mode, wire RealKitchenService
const kitchenProvider = chaosMode
    ? {
        provide: KitchenService,
        useFactory: () => {
            const chaos = new ChaosKitchenService();
            const withTimeout = new TimeoutKitchenService(chaos, 3000);
            return new FallbackKitchenService(withTimeout);
        },
    }
    : {provide: KitchenService, useClass: RealKitchenService};

@Module({
    providers: [kitchenProvider],
    exports: [KitchenService],
})
export class KitchenModule {
}
