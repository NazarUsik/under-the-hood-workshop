package resilience

import (
	"fmt"
	"time"

	"coffeeshop-chaos/kitchen"
)

// Exercise 3: Retry wrapper. Retries up to MaxAttempts times.
type RetryKitchenService struct {
	Inner       kitchen.KitchenService
	MaxAttempts int
	Delay       time.Duration
}

func (s *RetryKitchenService) Prepare(orderID int, drink string) (*kitchen.Preparation, error) {
	var lastErr error
	for attempt := 1; attempt <= s.MaxAttempts; attempt++ {
		prep, err := s.Inner.Prepare(orderID, drink)
		if err == nil {
			if attempt > 1 {
				fmt.Printf("[Retry] Succeeded on attempt %d for order #%d\n", attempt, orderID)
			}
			return prep, nil
		}
		lastErr = err
		fmt.Printf("[Retry] Attempt %d/%d failed for order #%d: %s\n", attempt, s.MaxAttempts, orderID, err)
		if attempt < s.MaxAttempts {
			time.Sleep(s.Delay)
		}
	}
	return nil, fmt.Errorf("all %d attempts failed for order #%d: %w", s.MaxAttempts, orderID, lastErr)
}
