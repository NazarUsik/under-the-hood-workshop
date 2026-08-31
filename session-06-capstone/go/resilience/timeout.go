package resilience

import (
	"context"
	"fmt"
	"time"

	"coffeeshop-capstone/kitchen"
)

type TimeoutKitchenService struct {
	Inner   kitchen.KitchenService
	Timeout time.Duration
}

func (s *TimeoutKitchenService) Prepare(orderID int, drink string) (*kitchen.Preparation, error) {
	ctx, cancel := context.WithTimeout(context.Background(), s.Timeout)
	defer cancel()

	type result struct {
		prep *kitchen.Preparation
		err  error
	}

	ch := make(chan result, 1)
	go func() {
		prep, err := s.Inner.Prepare(orderID, drink)
		ch <- result{prep, err}
	}()

	select {
	case r := <-ch:
		return r.prep, r.err
	case <-ctx.Done():
		return nil, fmt.Errorf("kitchen timed out after %s for order #%d", s.Timeout, orderID)
	}
}
