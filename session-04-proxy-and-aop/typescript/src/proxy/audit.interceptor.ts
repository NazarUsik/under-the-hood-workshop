import {CallHandler, ExecutionContext, Injectable, NestInterceptor} from "@nestjs/common";
import {Observable, throwError} from "rxjs";
import {catchError, tap} from "rxjs/operators";

// Audit interceptor: logs operation start, completion, and failure.
@Injectable()
export class AuditInterceptor implements NestInterceptor {
    intercept(context: ExecutionContext, next: CallHandler): Observable<any> {
        const handler = context.getHandler().name;

        console.log(`[Audit] operation started: ${handler}`);

        return next.handle().pipe(
            tap(() => {
                console.log(`[Audit] operation completed: ${handler}`);
            }),
            catchError((err) => {
                console.log(`[Audit] operation FAILED: ${handler} error=${err.message}`);
                return throwError(() => err);
            }),
        );
    }
}
