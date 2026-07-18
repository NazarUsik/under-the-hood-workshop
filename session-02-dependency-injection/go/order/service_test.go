package order

import "testing"

// Exercise 4: Unit test with a stub repository.
type stubOrderRepository struct{}

func (s *stubOrderRepository) FindAll() []Order {
	return []Order{
		{ID: 1, Drink: "Test Latte", Status: "ready"},
		{ID: 2, Drink: "Test Espresso", Status: "preparing"},
	}
}

func (s *stubOrderRepository) FindByID(id int) *Order {
	for _, o := range s.FindAll() {
		if o.ID == id {
			return &o
		}
	}
	return nil
}

func TestListOrders(t *testing.T) {
	service := NewOrderService(&stubOrderRepository{})
	orders := service.ListOrders()
	if len(orders) != 2 {
		t.Errorf("expected 2 orders, got %d", len(orders))
	}
	if orders[0].Drink != "Test Latte" {
		t.Errorf("expected Test Latte, got %s", orders[0].Drink)
	}
}

func TestFindOrder(t *testing.T) {
	service := NewOrderService(&stubOrderRepository{})
	order := service.FindOrder(2)
	if order == nil {
		t.Fatal("expected order, got nil")
	}
	if order.Drink != "Test Espresso" {
		t.Errorf("expected Test Espresso, got %s", order.Drink)
	}
}

func TestFindOrderMissing(t *testing.T) {
	service := NewOrderService(&stubOrderRepository{})
	order := service.FindOrder(99)
	if order != nil {
		t.Errorf("expected nil, got %+v", order)
	}
}
