import json
from datetime import datetime
from http.server import HTTPServer, BaseHTTPRequestHandler


MENU = [
    {"id": 1, "name": "Latte", "price": 4.50},
    {"id": 2, "name": "Espresso", "price": 3.00},
    {"id": 3, "name": "Cappuccino", "price": 4.00},
    {"id": 4, "name": "Americano", "price": 3.50},
]


class OrderHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path == "/orders":
            orders = [
                {"id": 1, "drink": "Latte"},
                {"id": 2, "drink": "Espresso"},
                {"id": 3, "drink": "Cappuccino"},
            ]
            self._json_response(orders)

        # Exercise 2: GET /menu - more manual routing and response work
        elif self.path == "/menu":
            self._json_response(MENU)

        # Exercise 4: An endpoint that raises an error
        elif self.path == "/error":
            # In library mode, this crashes the handler.
            # The server stays up but the client gets a broken response.
            raise RuntimeError("Something went wrong!")

        else:
            self.send_response(404)
            self.end_headers()

    def _json_response(self, data):
        response = json.dumps(data)
        self.send_response(200)
        self.send_header("Content-Type", "application/json")
        self.end_headers()
        self.wfile.write(response.encode())

    # Exercise 3: Request logging - you override log_message manually
    def log_message(self, format, *args):
        print(f"[{datetime.now()}] {self.command} {self.path}")


if __name__ == "__main__":
    # YOU create the server. YOU bind the port. YOU start the loop.
    server = HTTPServer(("", 8080), OrderHandler)
    print("Library server running on http://localhost:8080")
    print("Try: curl http://localhost:8080/orders")
    server.serve_forever()
