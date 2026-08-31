package middleware

import (
	"fmt"
	"net/http"

	"github.com/gin-gonic/gin"
)

// RecoveryMiddleware catches panics and returns a 500 JSON response.
// This is Go's version of exception handling middleware.
// Gin includes gin.Recovery() by default, but this shows how it works.
func RecoveryMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		defer func() {
			if err := recover(); err != nil {
				fmt.Printf("[Recovery] Caught panic: %v\n", err)
				c.AbortWithStatusJSON(http.StatusInternalServerError, gin.H{
					"error": "Internal server error",
				})
			}
		}()
		c.Next()
	}
}
