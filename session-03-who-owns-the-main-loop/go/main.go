package main

import (
	"context"
	"fmt"
	"net/http"
	"os"
	"os/signal"
	"syscall"
	"time"

	"coffeeshop-main-loop/menu"
	"coffeeshop-main-loop/middleware"
	"coffeeshop-main-loop/order"

	"github.com/gin-gonic/gin"
)

func main() {
	// --- Manual DI wiring (Go has no framework magic) ---
	orderRepo := order.NewInMemoryOrderRepository()
	orderService := order.NewOrderService(orderRepo)
	orderHandler := order.NewHandler(orderService)

	menuRepo := menu.NewInMemoryMenuRepository()
	menuService := menu.NewMenuService(menuRepo)
	menuHandler := menu.NewHandler(menuService)

	// --- Startup lifecycle ---
	fmt.Println("[Lifecycle] startup: services initialized")

	// --- Build the middleware pipeline ---
	r := gin.New() // gin.New() gives us a bare router (no default middleware)
	r.Use(middleware.RecoveryMiddleware())
	r.Use(middleware.RequestIDMiddleware())
	r.Use(middleware.LoggingMiddleware())
	r.Use(middleware.AuthMiddleware())
	// Middleware order: Recovery (outermost) -> RequestID -> Logging -> Auth -> Handler.

	// --- Routes ---
	r.GET("/orders", orderHandler.ListOrders)
	r.GET("/orders/:id", orderHandler.GetOrder)
	r.GET("/menu", menuHandler.ListMenu)

	// --- Graceful shutdown ---
	// In Go, you handle shutdown explicitly. No framework magic.
	srv := &http.Server{
		Addr:    ":8080",
		Handler: r,
	}

	go func() {
		fmt.Println("[Lifecycle] server listening on http://localhost:8080")
		if err := srv.ListenAndServe(); err != nil && err != http.ErrServerClosed {
			fmt.Printf("Server error: %v\n", err)
			os.Exit(1)
		}
	}()

	// Wait for interrupt signal
	quit := make(chan os.Signal, 1)
	signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
	<-quit

	fmt.Println("[Lifecycle] shutdown: received signal, shutting down gracefully")

	// Give in-flight requests 5 seconds to finish
	ctx, cancel := context.WithTimeout(context.Background(), 5*time.Second)
	defer cancel()
	if err := srv.Shutdown(ctx); err != nil {
		fmt.Printf("Server forced shutdown: %v\n", err)
	}

	fmt.Println("[Lifecycle] shutdown: server stopped")
}
