import {ArgumentsHost, Catch, ExceptionFilter, HttpException} from "@nestjs/common";
import {Response} from "express";

// NestJS exception filter: catches exceptions from the controller chain.
// This is the error handling middleware. The framework catches the error,
// finds this filter, and lets you control the response.
@Catch(HttpException)
export class HttpExceptionFilter implements ExceptionFilter {
    catch(exception: HttpException, host: ArgumentsHost) {
        const ctx = host.switchToHttp();
        const response = ctx.getResponse<Response>();
        const status = exception.getStatus();

        console.log(`[ExceptionFilter] ${status}: ${exception.message}`);

        response.status(status).json({
            statusCode: status,
            message: exception.message,
        });
    }
}
