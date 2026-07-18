import http from "http";

interface Order {
    id: number;
    drink: string;
}

interface MenuItem {
    id: number;
    name: string;
    price: number;
}

const orders: Order[] = [
    {id: 1, drink: "Latte"},
    {id: 2, drink: "Espresso"},
    {id: 3, drink: "Cappuccino"},
];

const menu: MenuItem[] = [
    {id: 1, name: "Latte", price: 4.50},
    {id: 2, name: "Espresso", price: 3.00},
    {id: 3, name: "Cappuccino", price: 4.00},
    {id: 4, name: "Americano", price: 3.50},
];

// YOU create the server. YOU check the URL. YOU write the response.
const server = http.createServer((req, res) => {
    // Exercise 3: Request logging - you add it manually at the top of the handler
    console.log(`[${new Date().toISOString()}] ${req.method} ${req.url}`);

    if (req.url === "/orders" && req.method === "GET") {
        res.writeHead(200, {"Content-Type": "application/json"});
        res.end(JSON.stringify(orders));

    // Exercise 2: GET /menu - more manual routing
    } else if (req.url === "/menu" && req.method === "GET") {
        res.writeHead(200, {"Content-Type": "application/json"});
        res.end(JSON.stringify(menu));

    // Exercise 4: An endpoint that throws an error
    } else if (req.url === "/error" && req.method === "GET") {
        // In library mode, an unhandled throw crashes the process entirely.
        throw new Error("Something went wrong!");

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
