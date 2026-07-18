package menu

import (
	"net/http"

	"github.com/gin-gonic/gin"
)

type Handler struct {
	service *MenuService
}

func NewHandler(service *MenuService) *Handler {
	return &Handler{service: service}
}

func (h *Handler) ListMenu(c *gin.Context) {
	c.JSON(http.StatusOK, h.service.ListItems())
}
