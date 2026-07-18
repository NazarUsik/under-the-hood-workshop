import json
from http.server import HTTPServer, BaseHTTPRequestHandler


class OrderHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path == "/orders":
            orders = [
                {"id": 1, "drink": "Latte"},
                {"id": 2, "drink": "Espresso"},
                {"id": 3, "drink": "Cappuccino"},
            ]

            response = json.dumps(orders)
            # YOU set the status code. YOU set the headers. YOU write the bytes.
            self.send_response(200)
            self.send_header("Content-Type", "application/json")
            self.end_headers()
            self.wfile.write(response.encode())
        else:
            self.send_response(404)
            self.end_headers()

    def log_message(self, format, *args):
        print(f"[Library] {args[0]}")


if __name__ == "__main__":
    # YOU create the server. YOU bind the port. YOU start the loop.
    server = HTTPServer(("", 8080), OrderHandler)
    print("Library server running on http://localhost:8080")
    print("Try: curl http://localhost:8080/orders")
    server.serve_forever()
