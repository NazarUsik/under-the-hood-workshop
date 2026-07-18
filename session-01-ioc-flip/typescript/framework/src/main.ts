import {NestFactory} from "@nestjs/core";
import {AppModule} from "./app.module";

async function bootstrap() {
    // NestFactory creates the application. The framework owns the lifecycle.
    const app = await NestFactory.create(AppModule);
    await app.listen(8080);
    console.log("Framework server running on http://localhost:8080");
    console.log("Try: curl http://localhost:8080/orders");
}

bootstrap();
