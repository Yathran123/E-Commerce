class OrderManagement:
    def __init__(self):
        self.products = {
            "P101": {"category": "Electronics", "price": 10000, "stock": 10},
            "P102": {"category": "Clothing", "price": 2000, "stock": 20},
            "P103": {"category": "Grocery", "price": 500, "stock": 50},
            "P104": {"category": "Books", "price": 800, "stock": 15}
        }
    def process_order(self, items, coupon=""):
        subtotal = 0
        for item in items:
            pid = item["product_id"]
            qty = item["quantity"]
            if pid not in self.products:
                return "Invalid Product"
            if qty <= 0:
                return "Invalid Quantity"
            if qty > self.products[pid]["stock"]:
                return "Out of Stock"
            subtotal += self.products[pid]["price"] * qty
        category_discount = 0
        for item in items:
            product = self.products[item["product_id"]]
            qty = item["quantity"]
            amount = product["price"] * qty
            if product["category"] == "Electronics":
                category_discount += amount * 0.10
            elif product["category"] == "Clothing":
                category_discount += amount * 0.15
            elif product["category"] == "Grocery":
                category_discount += amount * 0.05
        total_quantity = sum(item["quantity"] for item in items)
        bulk_discount = 0
        if total_quantity >= 10:
            bulk_discount = subtotal * 0.05
        coupon_discount = 0
        if coupon == "":
            coupon_discount = 0
        elif coupon == "SAVE10":
            coupon_discount = subtotal * 0.10
        elif coupon == "SAVE20":
            coupon_discount = subtotal * 0.20
        else:
            return "Invalid Coupon"
        total_discount = category_discount + bulk_discount + coupon_discount
        max_discount = subtotal * 0.30
        if total_discount > max_discount:
            total_discount = max_discount
        discounted_amount = subtotal - total_discount
        gst = discounted_amount * 0.18
        if discounted_amount >= 5000:
            shipping = 0
        else:
            shipping = 100
        final_amount = discounted_amount + gst + shipping
        return {
            "Subtotal": round(subtotal, 2),
            "Discount": round(total_discount, 2),
            "GST": round(gst, 2),
            "Shipping": shipping,
            "Final Amount": round(final_amount, 2)
        }
if __name__ == "__main__":
    
    order = OrderManagement()

    items = [
        {"product_id": "P101", "quantity": 1},
        {"product_id": "P102", "quantity": 2}
    ]

    result = order.process_order(items, "SAVE10")

    print("ORDER PROCESSING")
    print("================")
    print("Subtotal:", result["Subtotal"])
    print("Discount:", result["Discount"])
    print("GST:", result["GST"])
    print("Shipping:", result["Shipping"])
    print("Final Amount:", result["Final Amount"])
