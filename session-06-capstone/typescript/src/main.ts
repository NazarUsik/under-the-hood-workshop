import "reflect-metadata";
import {NestFactory} from "@nestjs/core";
import {AppModule} from "./app.module";

async function bootstrap() {
    const app = await NestFactory.create(AppModule, {logger: ["error", "warn", "log"]});
    await app.listen(8080);

    const chaosMode = process.env.CHAOS_MODE === "true";
    const mode = chaosMode ? "CHAOS" : "NORMAL";
    console.log(`Server running on http://localhost:8080 [${mode} mode]`);
    console.log("Try: curl -X POST http://localhost:8080/orders/1/prepare");
}

bootstrap();
