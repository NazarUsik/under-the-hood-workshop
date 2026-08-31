package main

import (
	"fmt"

	"coffeeshop-proxy-aop/menu"
	"coffeeshop-proxy-aop/order"
	"coffeeshop-proxy-aop/proxy"

	"github.com/gin-gonic/gin"
)

func main() {
	// --- Build the real services ---
	orderRepo := order.NewInMemoryOrderRepository()
	realOrderService := order.NewOrderService(orderRepo)

	menuRepo := menu.NewInMemoryMenuRepository()
	realMenuService := menu.NewMenuService(menuRepo)

	// --- Wrap with proxies (the Go way) ---
	// Proxy chain: Logging -> Timing -> real service
	// The outermost proxy (Logging) runs first and last.
	timedOrderService := &proxy.TimingOrderService{Inner: realOrderService}
	loggedOrderService := &proxy.LoggingOrderService{Inner: timedOrderService}

	timedMenuService := &proxy.TimingMenuService{Inner: realMenuService}
	loggedMenuService := &proxy.LoggingMenuService{Inner: timedMenuService}

	// --- Wire handlers with proxied services ---
	// The handler doesn't know it's talking to a proxy.
	// It just sees an OrderService interface.
	orderHandler := order.NewHandler(loggedOrderService)
	menuHandler := menu.NewHandler(loggedMenuService)

	// --- Routes ---
	r := gin.Default()
	r.GET("/orders", orderHandler.ListOrders)
	r.GET("/orders/:id", orderHandler.GetOrder)
	r.GET("/menu", menuHandler.ListMenu)

	fmt.Println("Server running on http://localhost:8080")
	fmt.Println("Try: curl http://localhost:8080/orders")
	r.Run(":8080")
}
