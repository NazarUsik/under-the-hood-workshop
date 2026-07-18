package main

import (
	"encoding/json"
	"fmt"
	"net/http"
)

type Order struct {
	ID    int    `json:"id"`
	Drink string `json:"drink"`
}

func main() {
	orders := []Order{
		{ID: 1, Drink: "Latte"},
		{ID: 2, Drink: "Espresso"},
		{ID: 3, Drink: "Cappuccino"},
	}

	// YOU register the handler. YOU set the headers. YOU encode the JSON.
	http.HandleFunc("/orders", func(w http.ResponseWriter, r *http.Request) {
		if r.Method != http.MethodGet {
			w.WriteHeader(http.StatusMethodNotAllowed)
			return
		}
		w.Header().Set("Content-Type", "application/json")
		json.NewEncoder(w).Encode(orders)
	})

	// YOU start the server. YOU own the loop.
	fmt.Println("Library server running on http://localhost:8080")
	fmt.Println("Try: curl http://localhost:8080/orders")
	http.ListenAndServe(":8080", nil)
}
