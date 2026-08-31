import {Injectable} from "@nestjs/common";

// Thread-safe step recorder. Only registered as a provider in test modules.
// Production has no TraceCollector.
@Injectable()
export class TraceCollector {
    private trace: string[] = [];

    add(step: string): void {
        this.trace.push(step);
    }

    getTrace(): string[] {
        return [...this.trace];
    }

    clear(): void {
        this.trace = [];
    }
}
