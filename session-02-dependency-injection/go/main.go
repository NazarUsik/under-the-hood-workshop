package main

import (
	"fmt"

	"coffeeshop-di/menu"
	"coffeeshop-di/order"

	"github.com/gin-gonic/gin"
)

func main() {
	// This IS dependency injection in Go. No framework, no annotations, no magic.
	// You create the dependencies and pass them to constructors. That's it.

	// Exercise 3: Swapped to PostgresOrderRepository. Just changed one line.
	orderRepo := order.NewPostgresOrderRepository()
	orderService := order.NewOrderService(orderRepo)
	orderHandler := order.NewHandler(orderService)

	// Exercise 2: Added menu chain.
	menuRepo := menu.NewInMemoryMenuRepository()
	menuService := menu.NewMenuService(menuRepo)
	menuHandler := menu.NewHandler(menuService)

	r := gin.Default()
	r.GET("/orders", orderHandler.ListOrders)
	r.GET("/orders/:id", orderHandler.GetOrder)
	r.GET("/menu", menuHandler.ListMenu)

	fmt.Println("Server running on http://localhost:8080")
	fmt.Println("Try: curl http://localhost:8080/orders")
	r.Run(":8080")
}
