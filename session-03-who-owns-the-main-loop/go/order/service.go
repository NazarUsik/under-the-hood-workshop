package order

type OrderService struct {
	repo OrderRepository
}

func NewOrderService(repo OrderRepository) *OrderService {
	return &OrderService{repo: repo}
}

func (s *OrderService) ListOrders() []Order {
	return s.repo.FindAll()
}

func (s *OrderService) FindOrder(id int) *Order {
	return s.repo.FindByID(id)
}
