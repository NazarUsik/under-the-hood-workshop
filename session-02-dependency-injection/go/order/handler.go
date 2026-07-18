package order

import (
	"net/http"
	"strconv"

	"github.com/gin-gonic/gin"
)

// Handler holds the HTTP handlers for order endpoints.
// It depends on OrderService, which is injected via NewHandler.
type Handler struct {
	service *OrderService
}

func NewHandler(service *OrderService) *Handler {
	return &Handler{service: service}
}

func (h *Handler) ListOrders(c *gin.Context) {
	c.JSON(http.StatusOK, h.service.ListOrders())
}

func (h *Handler) GetOrder(c *gin.Context) {
	id, err := strconv.Atoi(c.Param("id"))
	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"error": "invalid order ID"})
		return
	}

	order := h.service.FindOrder(id)
	if order == nil {
		c.JSON(http.StatusNotFound, gin.H{"error": "order not found"})
		return
	}
	c.JSON(http.StatusOK, order)
}
