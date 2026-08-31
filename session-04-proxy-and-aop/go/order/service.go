package order

// OrderService interface: this is what the proxy wraps.
// Both the real service and the proxy implement this interface.
type OrderService interface {
	ListOrders() []Order
	FindOrder(id int) *Order
}

// orderServiceImpl: the real service. NO logging, NO timing.
type orderServiceImpl struct {
	repo OrderRepository
}

func NewOrderService(repo OrderRepository) OrderService {
	return &orderServiceImpl{repo: repo}
}

func (s *orderServiceImpl) ListOrders() []Order {
	return s.repo.FindAll()
}

func (s *orderServiceImpl) FindOrder(id int) *Order {
	return s.repo.FindByID(id)
}
