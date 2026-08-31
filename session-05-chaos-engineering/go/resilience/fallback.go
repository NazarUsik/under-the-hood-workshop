package resilience

import (
	"fmt"

	"coffeeshop-chaos/kitchen"
)

// FallbackKitchenService catches errors and returns a "queued" fallback.
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
