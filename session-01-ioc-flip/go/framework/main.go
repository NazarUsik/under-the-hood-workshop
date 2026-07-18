package main

import (
	"fmt"

	"github.com/gin-gonic/gin"
)

type Order struct {
	ID    int    `json:"id"`
	Drink string `json:"drink"`
}

type MenuItem struct {
	ID    int     `json:"id"`
	Name  string  `json:"name"`
	Price float64 `json:"price"`
}

func main() {
	// gin.Default() already includes logging and panic recovery middleware.
	// Exercise 3 is solved for free - Gin logs every request automatically.
	r := gin.Default()

	orders := []Order{
		{ID: 1, Drink: "Latte"},
		{ID: 2, Drink: "Espresso"},
		{ID: 3, Drink: "Cappuccino"},
	}

	menu := []MenuItem{
		{ID: 1, Name: "Latte", Price: 4.50},
		{ID: 2, Name: "Espresso", Price: 3.00},
		{ID: 3, Name: "Cappuccino", Price: 4.00},
		{ID: 4, Name: "Americano", Price: 3.50},
	}

	// Gin calls this handler. You just registered it.
	r.GET("/orders", func(c *gin.Context) {
		c.JSON(200, orders)
	})

	// Exercise 2: GET /menu - one line to register, one line to respond.
	r.GET("/menu", func(c *gin.Context) {
		c.JSON(200, menu)
	})

	// Exercise 4: Panic in a handler. Gin's default Recovery middleware
	// catches the panic, logs it, and returns a 500 response. No crash.
	r.GET("/error", func(c *gin.Context) {
		panic("Something went wrong!")
	})

	fmt.Println("Framework server running on http://localhost:8080")
	fmt.Println("Try: curl http://localhost:8080/orders")
	r.Run(":8080")
}
