package middleware

import (
	"fmt"
	"time"

	"github.com/gin-gonic/gin"
)

// LoggingMiddleware logs every request before and after the handler.
// c.Next() passes control to the next handler in the chain.
// Code before c.Next() runs before your handler, code after runs after.
func LoggingMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		start := time.Now()
		fmt.Printf("[Middleware] >> %s %s\n", c.Request.Method, c.Request.URL.Path)

		c.Next()

		duration := time.Since(start)
		fmt.Printf("[Middleware] << %d (%s)\n", c.Writer.Status(), duration)
	}
}
