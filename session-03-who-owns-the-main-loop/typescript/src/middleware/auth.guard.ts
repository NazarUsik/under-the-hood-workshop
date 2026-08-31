import {Injectable, CanActivate, ExecutionContext} from "@nestjs/common";

// Exercise 3: Auth guard. Returns false to short-circuit: the handler never runs.
// NestJS returns 403 Forbidden by default when a guard returns false.
@Injectable()
export class AuthGuard implements CanActivate {
    canActivate(context: ExecutionContext): boolean {
        const request = context.switchToHttp().getRequest();
        const auth = request.headers["authorization"];
        if (!auth) {
            console.log("[Auth] Missing Authorization header, returning 403");
            return false;
        }
        console.log(`[Auth] Authorized: ${auth}`);
        return true;
    }
}
