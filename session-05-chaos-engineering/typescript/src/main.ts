import {NestFactory} from "@nestjs/core";
import {AppModule} from "./app.module";

async function bootstrap() {
    const app = await NestFactory.create(AppModule);
    const mode = process.env.CHAOS_MODE === "true" ? "CHAOS" : "NORMAL";

    await app.listen(8080);
    console.log(`Server running on http://localhost:8080 [${mode} mode]`);
    console.log("Try: curl -X POST http://localhost:8080/orders/1/prepare");
}

bootstrap();
