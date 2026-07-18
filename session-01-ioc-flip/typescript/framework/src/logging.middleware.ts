import {Injectable, NestMiddleware} from "@nestjs/common";
import {Request, Response, NextFunction} from "express";

// Exercise 3: Request logging via middleware.
// NestJS calls this for every request automatically.
// You don't touch your controllers. This is a preview of middleware (Session 3).
@Injectable()
export class LoggingMiddleware implements NestMiddleware {
    use(req: Request, _res: Response, next: NextFunction) {
        console.log(`[${new Date().toISOString()}] ${req.method} ${req.originalUrl}`);
        next();
    }
}
