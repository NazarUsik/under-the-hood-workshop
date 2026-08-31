package menu

type MenuRepository interface {
	FindAll() []MenuItem
}

type InMemoryMenuRepository struct {
	items []MenuItem
}

func NewInMemoryMenuRepository() *InMemoryMenuRepository {
	return &InMemoryMenuRepository{
		items: []MenuItem{
			{ID: 1, Name: "Latte", Price: 4.50},
			{ID: 2, Name: "Espresso", Price: 3.00},
			{ID: 3, Name: "Cappuccino", Price: 4.00},
			{ID: 4, Name: "Americano", Price: 3.50},
		},
	}
}

func (r *InMemoryMenuRepository) FindAll() []MenuItem {
	return r.items
}
