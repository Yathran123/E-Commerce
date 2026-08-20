from OrderManagement import OrderManagement
om = OrderManagement()
test_cases = [
    (1, [{"product_id": "P101", "quantity": 1}], ""),
    (2, [{"product_id": "P102", "quantity": 1}], ""),
    (3, [{"product_id": "P103", "quantity": 2}], ""),
    (4, [
        {"product_id": "P101", "quantity": 1},
        {"product_id": "P102", "quantity": 2}
    ], ""),
    (5, [
        {"product_id": "P102", "quantity": 2},
        {"product_id": "P103", "quantity": 3}
    ], ""),
    (6, [{"product_id": "P101", "quantity": 0}], ""),
    (7, [{"product_id": "P101", "quantity": -2}], ""),
    (8, [{"product_id": "P999", "quantity": 1}], ""),
    (9, [{"product_id": "P101", "quantity": 20}], ""),
    (10, [{"product_id": "P101", "quantity": 1}], "SAVE10"),
    (11, [{"product_id": "P102", "quantity": 2}], "SAVE20"),
    (12, [{"product_id": "P101", "quantity": 1}], "INVALID"),
    (13, [
        {"product_id": "P101", "quantity": 5},
        {"product_id": "P102", "quantity": 5}
    ], "SAVE20"),
    (14, [{"product_id": "P103", "quantity": 10}], ""),
    (15, [{"product_id": "P103", "quantity": 10}], "SAVE10"),
    (16, [{"product_id": "P104", "quantity": 2}], ""),
    (17, [{"product_id": "P101", "quantity": 1}], ""),
    (18, [
        {"product_id": "P101", "quantity": 1},
        {"product_id": "P103", "quantity": 5}
    ], "SAVE10"),
    (19, [
        {"product_id": "P102", "quantity": 5},
        {"product_id": "P103", "quantity": 5}
    ], ""),
    (20, [
        {"product_id": "P101", "quantity": 5},
        {"product_id": "P102", "quantity": 5},
        {"product_id": "P103", "quantity": 5}
    ], "SAVE20")
]
print("ORDER MANAGEMENT QA TESTING")
print("=" * 60)
for test_no, items, coupon in test_cases:
    result = om.process_order(items, coupon)
    print(f"\nTest Case {test_no}")
    print("-" * 30)
    print("Products:", items)
    print("Coupon:", coupon)
    if isinstance(result, str):
        print("Result:", result)
        print("Status: FAIL/VALIDATION")
    else:
        print("Subtotal:", result["Subtotal"])
        print("Discount:", result["Discount"])
        print("GST:", result["GST"])
        print("Shipping:", result["Shipping"])
        print("Final Amount:", result["Final Amount"])
        print("Status: PASS")
