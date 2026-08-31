package tracing

import (
	"fmt"

	"coffeeshop-capstone/kitchen"
)

// TracedKitchenService wraps any KitchenService and records before/after/error
// in the TraceCollector. Go's equivalent of AOP: struct composition with an interface.
// Zero lines added to any existing service.
type TracedKitchenService struct {
	Inner     kitchen.KitchenService
	Collector *TraceCollector
	Label     string // e.g. "RealKitchenService", "FallbackKitchenService"
}

func (s *TracedKitchenService) Prepare(orderID int, drink string) (*kitchen.Preparation, error) {
	step := fmt.Sprintf("%s.Prepare", s.Label)
	s.Collector.Add("before:" + step)

	prep, err := s.Inner.Prepare(orderID, drink)
	if err != nil {
		s.Collector.Add(fmt.Sprintf("error:%s:%s", step, err.Error()))
		return nil, err
	}

	s.Collector.Add("after:" + step)
	return prep, nil
}
