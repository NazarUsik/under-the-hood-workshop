package main

import (
	"fmt"

	"github.com/gin-gonic/gin"
)

type Order struct {
	ID    int    `json:"id"`
	Drink string `json:"drink"`
}

func main() {
	r := gin.Default()

	orders := []Order{
		{ID: 1, Drink: "Latte"},
		{ID: 2, Drink: "Espresso"},
		{ID: 3, Drink: "Cappuccino"},
	}

	// Gin calls this handler. You just registered it.
	r.GET("/orders", func(c *gin.Context) {
		c.JSON(200, orders)
	})

	fmt.Println("Framework server running on http://localhost:8080")
	fmt.Println("Try: curl http://localhost:8080/orders")
	r.Run(":8080")
}
