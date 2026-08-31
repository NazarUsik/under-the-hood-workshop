package resilience

import (
	"fmt"
	"sync"
	"time"

	"coffeeshop-capstone/kitchen"
)

// Exercise: Rate limiter wrapper. Rejects after MaxRequests in WindowDuration.
type RateLimiterKitchenService struct {
	Inner          kitchen.KitchenService
	MaxRequests    int
	WindowDuration time.Duration

	mu           sync.Mutex
	requestCount int
	windowStart  time.Time
}

func (s *RateLimiterKitchenService) Prepare(orderID int, drink string) (*kitchen.Preparation, error) {
	s.mu.Lock()
	now := time.Now()
	if s.windowStart.IsZero() || now.Sub(s.windowStart) > s.WindowDuration {
		s.windowStart = now
		s.requestCount = 0
	}
	s.requestCount++
	count := s.requestCount
	s.mu.Unlock()

	if count > s.MaxRequests {
		return nil, fmt.Errorf("rate limit exceeded: max %d requests per %s", s.MaxRequests, s.WindowDuration)
	}

	fmt.Printf("[RateLimiter] Request %d/%d\n", count, s.MaxRequests)
	return s.Inner.Prepare(orderID, drink)
}
