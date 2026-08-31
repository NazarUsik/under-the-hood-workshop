import {CallHandler, ExecutionContext, Injectable, NestInterceptor} from "@nestjs/common";
import {Observable} from "rxjs";
import {tap} from "rxjs/operators";

// NestJS Interceptor: wraps the handler execution.
// next.handle() is the "call next" that runs the controller method.
// Code before handle() runs before your handler, tap() runs after.
@Injectable()
export class TimingInterceptor implements NestInterceptor {
    intercept(context: ExecutionContext, next: CallHandler): Observable<any> {
        const req = context.switchToHttp().getRequest();
        const start = Date.now();
        console.log(`[Interceptor] >> ${req.method} ${req.path}`);

        return next.handle().pipe(
            tap(() => {
                const duration = Date.now() - start;
                console.log(`[Interceptor] << ${duration}ms`);
            }),
        );
    }
}
