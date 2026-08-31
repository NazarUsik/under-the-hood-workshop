import {Injectable, NestMiddleware} from "@nestjs/common";
import {Request, Response, NextFunction} from "express";
import {randomUUID} from "crypto";

// Exercise 2: Request ID middleware.
@Injectable()
export class RequestIdMiddleware implements NestMiddleware {
    use(req: Request, res: Response, next: NextFunction) {
        const requestId = randomUUID();
        res.setHeader("X-Request-Id", requestId);
        console.log(`[RequestId] ${requestId}`);
        next();
    }
}
