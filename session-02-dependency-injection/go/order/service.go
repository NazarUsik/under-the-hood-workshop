package order

// OrderService depends on OrderRepository (the interface, not a concrete type).
// The dependency is injected via the constructor function NewOrderService.
type OrderService struct {
	repo OrderRepository
}

// NewOrderService is the constructor. In Go, DI is just passing interfaces to constructors.
func NewOrderService(repo OrderRepository) *OrderService {
	return &OrderService{repo: repo}
}

func (s *OrderService) ListOrders() []Order {
	return s.repo.FindAll()
}

func (s *OrderService) FindOrder(id int) *Order {
	return s.repo.FindByID(id)
}
