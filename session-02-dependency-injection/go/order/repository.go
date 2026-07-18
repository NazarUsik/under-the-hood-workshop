package order

// OrderRepository is the interface. Any struct that implements these methods can be injected.
// Go interfaces are implicit: no "implements" keyword, no annotations, no decorators.
type OrderRepository interface {
	FindAll() []Order
	FindByID(id int) *Order
}

// InMemoryOrderRepository is the default implementation.
type InMemoryOrderRepository struct {
	orders []Order
}

func NewInMemoryOrderRepository() *InMemoryOrderRepository {
	return &InMemoryOrderRepository{
		orders: []Order{
			{ID: 1, Drink: "Latte", Status: "ready"},
			{ID: 2, Drink: "Espresso", Status: "preparing"},
			{ID: 3, Drink: "Cappuccino", Status: "pending"},
		},
	}
}

func (r *InMemoryOrderRepository) FindAll() []Order {
	return r.orders
}

func (r *InMemoryOrderRepository) FindByID(id int) *Order {
	for _, o := range r.orders {
		if o.ID == id {
			return &o
		}
	}
	return nil
}
