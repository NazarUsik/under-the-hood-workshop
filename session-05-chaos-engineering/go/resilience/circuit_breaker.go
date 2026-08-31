package resilience

import (
	"fmt"
	"time"

	"coffeeshop-chaos/kitchen"
)

// Exercise 4: Circuit breaker. Opens after FailureThreshold consecutive errors.
type CircuitBreakerKitchenService struct {
	Inner            kitchen.KitchenService
	FailureThreshold int
	OpenDuration     time.Duration

	consecutiveFailures int
	openedAt            time.Time
	isOpen              bool
}

func (s *CircuitBreakerKitchenService) Prepare(orderID int, drink string) (*kitchen.Preparation, error) {
	if s.isOpen {
		if time.Since(s.openedAt) > s.OpenDuration {
			fmt.Printf("[CircuitBreaker] Half-open: trying one request for order #%d\n", orderID)
			s.isOpen = false
			s.consecutiveFailures = 0
		} else {
			return nil, fmt.Errorf("circuit breaker is OPEN, rejecting order #%d", orderID)
		}
	}

	prep, err := s.Inner.Prepare(orderID, drink)
	if err != nil {
		s.consecutiveFailures++
		if s.consecutiveFailures >= s.FailureThreshold {
			s.isOpen = true
			s.openedAt = time.Now()
			fmt.Printf("[CircuitBreaker] OPENED after %d consecutive failures\n", s.consecutiveFailures)
		}
		return nil, err
	}

	s.consecutiveFailures = 0
	return prep, nil
}
