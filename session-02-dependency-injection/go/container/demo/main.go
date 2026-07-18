// Demonstrates manual DI in Go (the idiomatic way, no container needed).
// Go doesn't need a DI framework: interfaces + constructors = DI.
package main

import (
	"coffeeshop-di/order"
	"fmt"
)

func main() {
	// Manual DI: create dependencies, pass them to constructors.
	// This IS the Go way. No container, no reflection, no magic.
	repo := order.NewInMemoryOrderRepository()
	service := order.NewOrderService(repo)

	fmt.Println("Orders from manual DI (idiomatic Go):")
	for _, o := range service.ListOrders() {
		fmt.Printf("  #%d %s (%s)\n", o.ID, o.Drink, o.Status)
	}
}
