package order

import "coffeeshop-chaos/kitchen"

type OrderService struct {
	repo    OrderRepository
	kitchen kitchen.KitchenService
}

func NewOrderService(repo OrderRepository, kitchen kitchen.KitchenService) *OrderService {
	return &OrderService{repo: repo, kitchen: kitchen}
}

func (s *OrderService) ListOrders() []Order {
	return s.repo.FindAll()
}

func (s *OrderService) FindOrder(id int) *Order {
	return s.repo.FindByID(id)
}

func (s *OrderService) PlaceOrder(orderID int, drink string) (*kitchen.Preparation, error) {
	return s.kitchen.Prepare(orderID, drink)
}
