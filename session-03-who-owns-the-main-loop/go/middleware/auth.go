package middleware

import (
	"fmt"
	"net/http"

	"github.com/gin-gonic/gin"
)

// Exercise 3: Auth middleware. Checks for Authorization header.
// c.Abort() short-circuits: the handler never runs.
func AuthMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		auth := c.GetHeader("Authorization")
		if auth == "" {
			fmt.Println("[Auth] Missing Authorization header, returning 401")
			c.AbortWithStatusJSON(http.StatusUnauthorized, gin.H{"error": "unauthorized"})
			return
		}
		fmt.Printf("[Auth] Authorized: %s\n", auth)
		c.Next()
	}
}
