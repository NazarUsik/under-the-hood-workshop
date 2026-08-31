package proxy

import (
	"fmt"

	"coffeeshop-proxy-aop/menu"
)

// Exercise 2: Caching proxy for MenuService.
type CachingMenuService struct {
	Inner  menu.MenuService
	cached []menu.MenuItem
}

func (p *CachingMenuService) ListItems() []menu.MenuItem {
	if p.cached != nil {
		fmt.Println("[Cache] HIT for ListItems")
		return p.cached
	}
	fmt.Println("[Cache] MISS for ListItems")
	p.cached = p.Inner.ListItems()
	return p.cached
}
