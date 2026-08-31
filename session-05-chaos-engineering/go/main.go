package main

import (
	"fmt"
	"os"
	"time"

	"coffeeshop-chaos/kitchen"
	"coffeeshop-chaos/menu"
	"coffeeshop-chaos/order"
	"coffeeshop-chaos/resilience"

	"github.com/gin-gonic/gin"
)

func main() {
	chaosMode := os.Getenv("CHAOS_MODE") == "true"

	// --- Build kitchen service (swap point) ---
	var kitchenService kitchen.KitchenService
	if chaosMode {
		// Chaos mode: Fallback(Timeout(Chaos))
		chaos := &kitchen.ChaosKitchenService{}
		withTimeout := &resilience.TimeoutKitchenService{Inner: chaos, Timeout: 3 * time.Second}
		kitchenService = &resilience.FallbackKitchenService{Inner: withTimeout}
	} else {
		kitchenService = &kitchen.RealKitchenService{}
	}

	// --- Build services ---
	orderRepo := order.NewInMemoryOrderRepository()
	orderService := order.NewOrderService(orderRepo, kitchenService)
	orderHandler := order.NewHandler(orderService)

	menuRepo := menu.NewInMemoryMenuRepository()
	menuService := menu.NewMenuService(menuRepo)
	menuHandler := menu.NewHandler(menuService)

	// --- Routes ---
	r := gin.Default()
	r.GET("/orders", orderHandler.ListOrders)
	r.GET("/orders/:id", orderHandler.GetOrder)
	r.POST("/orders/:id/prepare", orderHandler.PrepareOrder)
	r.GET("/menu", menuHandler.ListMenu)

	mode := "NORMAL"
	if chaosMode {
		mode = "CHAOS"
	}
	fmt.Printf("Server running on http://localhost:8080 [%s mode]\n", mode)
	fmt.Println("Try: curl -X POST http://localhost:8080/orders/1/prepare")
	r.Run(":8080")
}
