import {NestFactory} from "@nestjs/core";
import {AppModule} from "./app.module";

async function bootstrap() {
    const app = await NestFactory.create(AppModule);
    await app.listen(8080);
    console.log("Server running on http://localhost:8080");
    console.log("Try: curl http://localhost:8080/orders");
}

bootstrap();
