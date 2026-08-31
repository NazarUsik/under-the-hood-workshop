package kitchen

import (
	"fmt"
	"math/rand"
	"time"
)

type Preparation struct {
	OrderID int    `json:"orderId"`
	Drink   string `json:"drink"`
	Status  string `json:"status"`
}

type KitchenService interface {
	Prepare(orderID int, drink string) (*Preparation, error)
}

type RealKitchenService struct{}

func (s *RealKitchenService) Prepare(orderID int, drink string) (*Preparation, error) {
	return &Preparation{OrderID: orderID, Drink: drink, Status: "preparing"}, nil
}

type ChaosKitchenService struct{}

func (s *ChaosKitchenService) Prepare(orderID int, drink string) (*Preparation, error) {
	roll := rand.Float64()

	if roll < 0.3 {
		return nil, fmt.Errorf("kitchen equipment malfunction! Order #%d failed", orderID)
	}

	if roll < 0.5 {
		delay := time.Duration(2000+rand.Intn(4000)) * time.Millisecond
		fmt.Printf("[Chaos] Injecting %s latency for order #%d\n", delay, orderID)
		time.Sleep(delay)
	}

	if roll < 0.6 {
		fmt.Printf("[Chaos] Returning bad data for order #%d\n", orderID)
		return &Preparation{OrderID: orderID, Drink: drink, Status: "UNKNOWN_STATUS"}, nil
	}

	return &Preparation{OrderID: orderID, Drink: drink, Status: "preparing"}, nil
}
