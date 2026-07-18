from fastapi import FastAPI

app = FastAPI()


@app.get("/orders")
def list_orders():
    # FastAPI calls this function when GET /orders arrives.
    # You never call it yourself. That's IoC.
    return [
        {"id": 1, "drink": "Latte"},
        {"id": 2, "drink": "Espresso"},
        {"id": 3, "drink": "Cappuccino"},
    ]
