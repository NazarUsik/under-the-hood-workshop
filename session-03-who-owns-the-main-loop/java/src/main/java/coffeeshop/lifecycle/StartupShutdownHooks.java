package coffeeshop.lifecycle;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

// Lifecycle hooks: Spring calls these at specific points in the application lifecycle.
// You never call them yourself. The framework owns the lifecycle.
@Component
public class StartupShutdownHooks {

    // Called after DI is complete and the bean is fully initialized.
    // Use for: validation, cache warming, initial data loading.
    @PostConstruct
    public void onInit() {
        System.out.println("[Lifecycle] @PostConstruct: bean initialized, dependencies injected");
    }

    // Called when the application is fully started and ready to accept requests.
    // Use for: logging readiness, health check signaling.
    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        System.out.println("[Lifecycle] ApplicationReadyEvent: server is ready for traffic");
    }

    // Called during graceful shutdown (SIGTERM, Ctrl+C).
    // Use for: closing connections, flushing caches, cleanup.
    @PreDestroy
    public void onShutdown() {
        System.out.println("[Lifecycle] @PreDestroy: shutting down, cleaning up resources");
    }
}
