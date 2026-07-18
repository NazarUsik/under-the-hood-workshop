package main

import (
	"fmt"

	"coffeeshop-di/order"

	"github.com/gin-gonic/gin"
)

func main() {
	// This IS dependency injection in Go. No framework, no annotations, no magic.
	// You create the dependencies and pass them to constructors. That's it.
	repo := order.NewInMemoryOrderRepository()
	service := order.NewOrderService(repo)
	handler := order.NewHandler(service)

	r := gin.Default()
	r.GET("/orders", handler.ListOrders)
	r.GET("/orders/:id", handler.GetOrder)

	fmt.Println("Server running on http://localhost:8080")
	fmt.Println("Try: curl http://localhost:8080/orders")
	r.Run(":8080")
}
