package proxy

import (
	"fmt"

	"coffeeshop-proxy-aop/order"
	"coffeeshop-proxy-aop/menu"
)

// LoggingOrderService is a proxy that wraps OrderService.
// It implements the same interface, so the caller can't tell the difference.
// This is the Proxy pattern in its purest form: no magic, no reflection.
type LoggingOrderService struct {
	Inner order.OrderService
}

func (p *LoggingOrderService) ListOrders() []order.Order {
	fmt.Printf("[Logging] >> OrderService.ListOrders()\n")
	result := p.Inner.ListOrders()
	fmt.Printf("[Logging] << OrderService.ListOrders() returned %d orders\n", len(result))
	return result
}

func (p *LoggingOrderService) FindOrder(id int) *order.Order {
	fmt.Printf("[Logging] >> OrderService.FindOrder(id=%d)\n", id)
	result := p.Inner.FindOrder(id)
	fmt.Printf("[Logging] << OrderService.FindOrder() returned=%v\n", result)
	return result
}

// LoggingMenuService wraps MenuService with logging.
type LoggingMenuService struct {
	Inner menu.MenuService
}

func (p *LoggingMenuService) ListItems() []menu.MenuItem {
	fmt.Printf("[Logging] >> MenuService.ListItems()\n")
	result := p.Inner.ListItems()
	fmt.Printf("[Logging] << MenuService.ListItems() returned %d items\n", len(result))
	return result
}
