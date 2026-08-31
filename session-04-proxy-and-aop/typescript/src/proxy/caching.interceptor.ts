import {CallHandler, ExecutionContext, Injectable, NestInterceptor} from "@nestjs/common";
import {Observable, of} from "rxjs";
import {tap} from "rxjs/operators";

// Exercise 2: Caching interceptor. Caches response by URL.
@Injectable()
export class CachingInterceptor implements NestInterceptor {
    private cache = new Map<string, any>();

    intercept(context: ExecutionContext, next: CallHandler): Observable<any> {
        const req = context.switchToHttp().getRequest();
        const key = req.url;

        if (this.cache.has(key)) {
            console.log(`[Cache] HIT for ${key}`);
            return of(this.cache.get(key));
        }

        console.log(`[Cache] MISS for ${key}`);
        return next.handle().pipe(
            tap((data) => {
                this.cache.set(key, data);
            }),
        );
    }
}
