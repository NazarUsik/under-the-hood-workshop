package proxy

import (
	"fmt"

	"coffeeshop-proxy-aop/order"
)

// Exercise 3: Validating proxy for OrderService.
type ValidatingOrderService struct {
	Inner order.OrderService
}

func (p *ValidatingOrderService) ListOrders() []order.Order {
	return p.Inner.ListOrders()
}

func (p *ValidatingOrderService) FindOrder(id int) *order.Order {
	if id <= 0 {
		fmt.Printf("[Validation] Order ID must be positive, got: %d\n", id)
		return nil
	}
	return p.Inner.FindOrder(id)
}
