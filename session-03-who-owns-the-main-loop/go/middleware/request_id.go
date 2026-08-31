package middleware

import (
	"crypto/rand"
	"fmt"

	"github.com/gin-gonic/gin"
)

// Exercise 2: Request ID middleware. Generates a unique ID per request.
func RequestIDMiddleware() gin.HandlerFunc {
	return func(c *gin.Context) {
		b := make([]byte, 16)
		rand.Read(b)
		requestID := fmt.Sprintf("%x-%x-%x-%x-%x", b[0:4], b[4:6], b[6:8], b[8:10], b[10:])
		c.Writer.Header().Set("X-Request-Id", requestID)
		fmt.Printf("[RequestId] %s\n", requestID)
		c.Next()
	}
}
