package com.ecommerce.order;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderManagement implements Serializable {
    private static final long serialVersionUID = 1L;

    // Simulated Central Inventory Database [Product ID -> Stock Count]
    private static final Map<String, Integer> INVENTORY_DB = new HashMap<>();
    static {
        INVENTORY_DB.put("PROD-001", 50);  // Electronics
        INVENTORY_DB.put("PROD-002", 100); // Clothing
        INVENTORY_DB.put("PROD-003", 5);   // Low Stock Item
        INVENTORY_DB.put("PROD-004", 0);   // Out of Stock Item
    }

    public static class OrderItem {
        public String productId;
        public String category; // "ELECTRONICS", "CLOTHING", "BOOKS"
        public int quantity;
        public double unitPrice;
        public double baseDiscountPercent; // e.g., 5.0 for 5%
        public double taxPercent;          // e.g., 18.0 for 18% GST

        public OrderItem(String pId, String cat, int qty, double price, double disc, double tax) {
            this.productId = pId;
            this.category = cat;
            this.quantity = qty;
            this.unitPrice = price;
            this.baseDiscountPercent = disc;
            this.taxPercent = tax;
        }
    }

    public static class OrderResponse {
        public double subtotal = 0.0;
        public double categoryDiscount = 0.0;
        public double bulkDiscount = 0.0;
        public double couponDiscount = 0.0;
        public double totalDiscount = 0.0;
        public double gst = 0.0;
        public double shippingCharge = 0.0;
        public double finalAmount = 0.0;
        public boolean isProcessed = false;
        public String statusMessage = "";
    }

    public OrderResponse processOrder(List<OrderItem> items, String couponCode) {
        OrderResponse response = new OrderResponse();
        
        if (items == null || items.isEmpty()) {
            response.statusMessage = "Order rejected: Shopping cart is empty.";
            return response;
        }

        int totalItemsCount = 0;

        // 1. Core Item Validation and Subtotal Accumulation Loop
        for (OrderItem item : items) {
            // Validation: Invalid Product Entry Check
            if (!INVENTORY_DB.containsKey(item.productId)) {
                response.statusMessage = "Order rejected: Invalid Product ID (" + item.productId + ").";
                return response;
            }
            // Validation: Negative or Zero Quantities Check
            if (item.quantity < 0) {
                throw new IllegalArgumentException("Quantity cannot be negative.");
            }
            if (item.quantity == 0) {
                response.statusMessage = "Order rejected: Product quantity cannot be zero.";
                return response;
            }
            // Validation: Out of Stock Check
            if (INVENTORY_DB.get(item.productId) < item.quantity) {
                response.statusMessage = "Order rejected: Product " + item.productId + " is out of stock.";
                return response;
            }

            double itemSubtotal = item.unitPrice * item.quantity;
            response.subtotal += itemSubtotal;
            totalItemsCount += item.quantity;

            // Category-Specific Base Discount Processing
            if ("ELECTRONICS".equalsIgnoreCase(item.category)) {
                response.categoryDiscount += itemSubtotal * 0.05; // 5% category perk
            } else if ("CLOTHING".equalsIgnoreCase(item.category)) {
                response.categoryDiscount += itemSubtotal * 0.10; // 10% category perk
            }

            // Calculation: Item Specific Base Tax (GST)
            double itemDiscount = itemSubtotal * (item.baseDiscountPercent / 100.0);
            double taxableItemValue = itemSubtotal - itemDiscount;
            response.gst += taxableItemValue * (item.taxPercent / 100.0);
        }

        // 2. Calculation: Bulk Order Discount Eligibility (More than 10 total retail items)
        if (totalItemsCount > 10) {
            response.bulkDiscount = response.subtotal * 0.07; // 7% wholesale price flat reduction
        }

        // 3. Calculation: Coupon Validation Logic
        if (couponCode != null && !couponCode.trim().isEmpty()) {
            if ("SAVE20".equalsIgnoreCase(couponCode)) {
                response.couponDiscount = response.subtotal * 0.20;
            } else if ("SUPER50".equalsIgnoreCase(couponCode)) {
                response.couponDiscount = response.subtotal * 0.50;
            } else {
                response.statusMessage = "Order rejected: Invalid coupon code applied.";
                return response;
            }
        }

        // 4. Verification: Apply Maximum Discount Ceiling Limit Rule (Capped at $500 max)
        response.totalDiscount = response.categoryDiscount + response.bulkDiscount + response.couponDiscount;
        if (response.totalDiscount > 500.0) {
            response.totalDiscount = 500.0;
        }

        // 5. Calculation: Free Shipping Threshold Logic (Threshold set at $150.00 scale)
        double netBeforeShipping = response.subtotal - response.totalDiscount + response.gst;
        if (netBeforeShipping >= 150.0 || netBeforeShipping <= 0.0) {
            response.shippingCharge = 0.0; // Free shipping threshold matched
        } else {
            response.shippingCharge = 15.00; // Flat base dynamic logistics shipping fee
        }

        // 6. Final Total Resolution Balance Calculation
        response.finalAmount = netBeforeShipping + response.shippingCharge;
        if (response.finalAmount < 0) {
            response.finalAmount = 0.0;
        }

        response.isProcessed = true;
        response.statusMessage = "Order processed successfully.";
        return response;
    }
}
