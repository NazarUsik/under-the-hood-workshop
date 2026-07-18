// Package container provides a minimal DI container using Go's reflect package.
// This is NOT idiomatic Go (manual wiring is preferred), but it demonstrates
// that the same algorithm used by Spring and NestJS can be built in Go.
package container

import (
	"fmt"
	"reflect"
)

type Container struct {
	registry   map[reflect.Type]reflect.Type  // interface -> implementation
	instances  map[reflect.Type]reflect.Value  // singleton cache
	inProgress map[reflect.Type]bool           // cycle detection
}

func New() *Container {
	return &Container{
		registry:   make(map[reflect.Type]reflect.Type),
		instances:  make(map[reflect.Type]reflect.Value),
		inProgress: make(map[reflect.Type]bool),
	}
}

// Register maps an interface type to an implementation type.
// Pass pointers to zero values: container.Register((*OrderRepository)(nil), (*InMemoryOrderRepository)(nil))
func (c *Container) Register(iface any, impl any) {
	ifaceType := reflect.TypeOf(iface).Elem()
	implType := reflect.TypeOf(impl).Elem()
	c.registry[ifaceType] = implType
}

// Resolve creates an instance of the requested type, resolving all constructor dependencies.
func (c *Container) Resolve(target any) any {
	targetType := reflect.TypeOf(target).Elem()
	return c.resolve(targetType).Interface()
}

func (c *Container) resolve(t reflect.Type) reflect.Value {
	// Singleton: already created?
	if instance, ok := c.instances[t]; ok {
		return instance
	}

	// Circular dependency detection
	if c.inProgress[t] {
		panic(fmt.Sprintf("circular dependency detected: %s", t.Name()))
	}
	c.inProgress[t] = true

	// Look up implementation
	implType, ok := c.registry[t]
	if !ok {
		implType = t
	}

	// Create instance (no-arg constructor for simplicity)
	instance := reflect.New(implType).Elem()

	c.instances[t] = instance
	delete(c.inProgress, t)
	return instance
}
