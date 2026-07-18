package order

// Exercise 3: Swap implementation. Change one line in main.go to use this.
type PostgresOrderRepository struct {
	orders []Order
}

func NewPostgresOrderRepository() *PostgresOrderRepository {
	return &PostgresOrderRepository{
		orders: []Order{
			{ID: 1, Drink: "Flat White", Status: "ready"},
			{ID: 2, Drink: "Mocha", Status: "preparing"},
			{ID: 3, Drink: "Cold Brew", Status: "pending"},
			{ID: 4, Drink: "Matcha Latte", Status: "ready"},
		},
	}
}

func (r *PostgresOrderRepository) FindAll() []Order {
	return r.orders
}

func (r *PostgresOrderRepository) FindByID(id int) *Order {
	for _, o := range r.orders {
		if o.ID == id {
			return &o
		}
	}
	return nil
}
