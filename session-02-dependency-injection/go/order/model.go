package order

type Order struct {
	ID     int    `json:"id"`
	Drink  string `json:"drink"`
	Status string `json:"status"`
}
