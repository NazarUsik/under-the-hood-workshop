import {NestFactory} from "@nestjs/core";
import {AppModule} from "./app.module";
import {TimingInterceptor} from "./middleware/timing.interceptor";
import {HttpExceptionFilter} from "./middleware/http-exception.filter";
import {AuthGuard} from "./middleware/auth.guard";

async function bootstrap() {
    const app = await NestFactory.create(AppModule);

    // Global interceptor: wraps every handler with timing
    app.useGlobalInterceptors(new TimingInterceptor());

    // Global exception filter: catches HttpExceptions from any controller
    app.useGlobalFilters(new HttpExceptionFilter());

    // Exercise 3: Global auth guard
    app.useGlobalGuards(new AuthGuard());

    // The framework creates the app, wires DI, registers middleware,
    // and starts listening. You just call bootstrap().
    await app.listen(8080);
    console.log("Server running on http://localhost:8080");
    console.log("Try: curl http://localhost:8080/orders");
}

bootstrap();
