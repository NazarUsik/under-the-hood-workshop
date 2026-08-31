package proxy

import (
	"fmt"
	"time"

	"coffeeshop-proxy-aop/order"
	"coffeeshop-proxy-aop/menu"
)

// TimingOrderService wraps OrderService with timing.
type TimingOrderService struct {
	Inner order.OrderService
}

func (p *TimingOrderService) ListOrders() []order.Order {
	start := time.Now()
	result := p.Inner.ListOrders()
	fmt.Printf("[Timing] ListOrders took %s\n", time.Since(start))
	return result
}

func (p *TimingOrderService) FindOrder(id int) *order.Order {
	start := time.Now()
	result := p.Inner.FindOrder(id)
	fmt.Printf("[Timing] FindOrder took %s\n", time.Since(start))
	return result
}

// TimingMenuService wraps MenuService with timing.
type TimingMenuService struct {
	Inner menu.MenuService
}

func (p *TimingMenuService) ListItems() []menu.MenuItem {
	start := time.Now()
	result := p.Inner.ListItems()
	fmt.Printf("[Timing] ListItems took %s\n", time.Since(start))
	return result
}
