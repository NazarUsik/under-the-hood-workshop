import http from "http";

interface Order {
    id: number;
    drink: string;
}

const orders: Order[] = [
    {id: 1, drink: "Latte"},
    {id: 2, drink: "Espresso"},
    {id: 3, drink: "Cappuccino"},
];

// YOU create the server. YOU check the URL. YOU write the response.
const server = http.createServer((req, res) => {
    if (req.url === "/orders" && req.method === "GET") {
        res.writeHead(200, {"Content-Type": "application/json"});
        res.end(JSON.stringify(orders));
    } else {
        res.writeHead(404);
        res.end();
    }
});

// YOU start the server. YOU own the loop.
server.listen(8080, () => {
    console.log("Library server running on http://localhost:8080");
    console.log("Try: curl http://localhost:8080/orders");
});
