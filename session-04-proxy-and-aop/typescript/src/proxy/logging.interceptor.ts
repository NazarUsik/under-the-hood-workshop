import {CallHandler, ExecutionContext, Injectable, NestInterceptor} from "@nestjs/common";
import {Observable} from "rxjs";
import {tap} from "rxjs/operators";

// NestJS interceptor = proxy at the request level.
// next.handle() is the "call the real method" step (like joinPoint.proceed()).
// Code before handle() is "before advice", tap() is "after advice".
@Injectable()
export class LoggingInterceptor implements NestInterceptor {
    intercept(context: ExecutionContext, next: CallHandler): Observable<any> {
        const handler = context.getHandler().name;
        const controller = context.getClass().name;
        const req = context.switchToHttp().getRequest();
        const args = req.params;

        console.log(`[Logging] >> ${controller}.${handler}(args=${JSON.stringify(args)})`);

        return next.handle().pipe(
            tap((data) => {
                console.log(`[Logging] << ${controller}.${handler} returned=${JSON.stringify(data)}`);
            }),
        );
    }
}
