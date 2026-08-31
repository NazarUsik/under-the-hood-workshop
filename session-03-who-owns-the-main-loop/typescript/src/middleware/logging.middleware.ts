import {Injectable, NestMiddleware} from "@nestjs/common";
import {NextFunction, Request, Response} from "express";

// NestJS middleware: Express-style (req, res, next).
// next() passes control to the next middleware or the route handler.
// This is the outermost layer of the NestJS request pipeline.
@Injectable()
export class LoggingMiddleware implements NestMiddleware {
    use(req: Request, res: Response, next: NextFunction) {
        const start = Date.now();
        console.log(`[Middleware] >> ${req.method} ${req.path}`);

        res.on("finish", () => {
            const duration = Date.now() - start;
            console.log(`[Middleware] << ${res.statusCode} (${duration}ms)`);
        });

        next();
    }
}
