package resilience

import (
	"fmt"

	"coffeeshop-capstone/kitchen"
)

type FallbackKitchenService struct {
	Inner kitchen.KitchenService
}

func (s *FallbackKitchenService) Prepare(orderID int, drink string) (*kitchen.Preparation, error) {
	prep, err := s.Inner.Prepare(orderID, drink)
	if err != nil {
		fmt.Printf("[Fallback] Kitchen failed: %s -- returning queued for order #%d\n", err, orderID)
		return &kitchen.Preparation{OrderID: orderID, Drink: drink, Status: "queued"}, nil
	}
	return prep, nil
}
