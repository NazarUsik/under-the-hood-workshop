package main

import (
	"encoding/json"
	"fmt"
	"log"
	"net/http"
	"time"
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

// Exercise 3: Request logging - you write a wrapper function manually.
// Every handler you want to log must be wrapped individually.
func withLogging(next http.HandlerFunc) http.HandlerFunc {
	return func(w http.ResponseWriter, r *http.Request) {
		log.Printf("[%s] %s %s", time.Now().Format(time.RFC3339), r.Method, r.URL.Path)
		next(w, r)
	}
}

func main() {
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

	// YOU register the handler. YOU set the headers. YOU encode the JSON.
	http.HandleFunc("/orders", withLogging(func(w http.ResponseWriter, r *http.Request) {
		if r.Method != http.MethodGet {
			w.WriteHeader(http.StatusMethodNotAllowed)
			return
		}
		w.Header().Set("Content-Type", "application/json")
		json.NewEncoder(w).Encode(orders)
	}))

	// Exercise 2: GET /menu - more manual routing and response work
	http.HandleFunc("/menu", withLogging(func(w http.ResponseWriter, r *http.Request) {
		if r.Method != http.MethodGet {
			w.WriteHeader(http.StatusMethodNotAllowed)
			return
		}
		w.Header().Set("Content-Type", "application/json")
		json.NewEncoder(w).Encode(menu)
	}))

	// Exercise 4: An endpoint that panics.
	// In library mode, net/http recovers panics per-goroutine,
	// but the client gets no response body.
	http.HandleFunc("/error", withLogging(func(w http.ResponseWriter, r *http.Request) {
		panic("Something went wrong!")
	}))

	// YOU start the server. YOU own the loop.
	fmt.Println("Library server running on http://localhost:8080")
	fmt.Println("Try: curl http://localhost:8080/orders")
	http.ListenAndServe(":8080", nil)
}
