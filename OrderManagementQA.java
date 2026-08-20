package com.ecommerce.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class OrderManagementQA {

    private OrderManagement engine;
    private List<OrderManagement.OrderItem> cart;

   
    public void setUp() {
        engine = new OrderManagement();
        cart = new ArrayList<>();
    }

   
    public void testCombination01_SingleProductSuccess() {
        cart.add(new OrderManagement.OrderItem("PROD-001", "ELECTRONICS", 1, 100.0, 0.0, 18.0));
        OrderManagement.OrderResponse res = engine.processOrder(cart, "");
        assertTrue(res.isProcessed);
        assertEquals(100.0, res.subtotal);
    }

    
    public void testCombination02_MultipleProductsSuccess() {
        cart.add(new OrderManagement.OrderItem("PROD-001", "ELECTRONICS", 1, 100.0, 0.0, 18.0));
        cart.add(new OrderManagement.OrderItem("PROD-002", "CLOTHING", 2, 50.0, 0.0, 5.0));
        OrderManagement.OrderResponse res = engine.processOrder(cart, "");
        assertTrue(res.isProcessed);
        assertEquals(200.0, res.subtotal);
    }

   
    public void testCombination03_ZeroQuantityRejection() {
        cart.add(new OrderManagement.OrderItem("PROD-001", "ELECTRONICS", 0, 100.0, 0.0, 18.0));
        OrderManagement.OrderResponse res = engine.processOrder(cart, "");
        assertFalse(res.isProcessed);
        assertEquals("Order rejected: Product quantity cannot be zero.", res.statusMessage);
    }

    
    public void testCombination04_NegativeQuantityException() {
        cart.add(new OrderManagement.OrderItem("PROD-001", "ELECTRONICS", -3, 100.0, 0.0, 18.0));
        assertThrows(IllegalArgumentException.class, () -> engine.processOrder(cart, ""));
    }

   
    public void testCombination05_InvalidProductRejection() {
        cart.add(new OrderManagement.OrderItem("FAKE-999", "BOOKS", 1, 20.0, 0.0, 5.0));
        OrderManagement.OrderResponse res = engine.processOrder(cart, "");
        assertFalse(res.isProcessed);
        assertTrue(res.statusMessage.contains("Invalid Product ID"));
    }

   
    public void testCombination06_InvalidCouponRejection() {
        cart.add(new OrderManagement.OrderItem("PROD-001", "ELECTRONICS", 1, 100.0, 0.0, 18.0));
        OrderManagement.OrderResponse res = engine.processOrder(cart, "FAKECOUPON");
        assertFalse(res.isProcessed);
        assertEquals("Order rejected: Invalid coupon code applied.", res.statusMessage);
    }

    
    public void testCombination07_ValidCouponApplication() {
        cart.add(new OrderManagement.OrderItem("PROD-001", "ELECTRONICS", 1, 100.0, 0.0, 0.0));
        OrderManagement.OrderResponse res = engine.processOrder(cart, "SAVE20");
        assertTrue(res.isProcessed);
        assertEquals(20.0, res.couponDiscount);
    }

    
    public void testCombination08_MaximumDiscountCeiling() {
        // High price triggers a massive cumulative discount calculation value
        cart.add(new OrderManagement.OrderItem("PROD-001", "ELECTRONICS", 1, 2000.0, 0.0, 0.0));
        OrderManagement.OrderResponse res = engine.processOrder(cart, "SUPER50");
        assertTrue(res.isProcessed);
        assertEquals(500.0, res.totalDiscount); // Capped flat ceiling restriction threshold
    }

    
    public void testCombination09_TaxCalculationAccuracy() {
        cart.add(new OrderManagement.OrderItem("PROD-002", "CLOTHING", 1, 100.0, 10.0, 10.0));
        OrderManagement.OrderResponse res = engine.processOrder(cart, "");
        // Tax Base Value = 100 - (10% Base Discount) = 90. 10% Tax of 90 = 9.00
        assertEquals(9.00, res.gst);
    }

   
    public void testCombination10_FreeShippingThresholdMatched() {
        cart.add(new OrderManagement.OrderItem("PROD-001", "ELECTRONICS", 2, 200.0, 0.0, 0.0));
        OrderManagement.OrderResponse res = engine.processOrder(cart, "");
        assertTrue(res.isProcessed);
        assertEquals(0.0, res.shippingCharge);
    }

    
    public void testCombination11_ShippingFeeAppliedBelowThreshold() {
        cart.add(new OrderManagement.OrderItem("PROD-003", "BOOKS", 1, 25.0, 0.0, 0.0));
        OrderManagement.OrderResponse res = engine.processOrder(cart, "");
        assertTrue(res.isProcessed);
        assertEquals(15.00, res.shippingCharge);
    }

    
    public void testCombination12_BulkOrderDiscountTriggered() {
        cart.add(new OrderManagement.OrderItem("PROD-002", "CLOTHING", 12, 10.0, 0.0, 0.0));
        OrderManagement.OrderResponse res = engine.processOrder(cart, "");
        assertTrue(res.isProcessed);
        assertTrue(res.bulkDiscount > 0.0);
    }

   
    public void testCombination13_ProductOutOfStockRejection() {
        cart.add(new OrderManagement.OrderItem("PROD-004", "ELECTRONICS", 1, 500.0, 0.0, 0.0));
        OrderManagement.OrderResponse res = engine.processOrder(cart, "");
        assertFalse(res.isProcessed);
        assertTrue(res.statusMessage.contains("out of stock"));
    }

   
    public void testCombination14_InsufficientStockLevelRejection() {
        // PROD-003 only has an active storage stock registry availability maximum capacity count of 5 items
        cart.add(new OrderManagement.OrderItem("PROD-003", "ELECTRONICS", 10, 50.0, 0.0, 0.0));
        OrderManagement.OrderResponse res = engine.processOrder(cart, "");
        assertFalse(res.isProcessed);
    }

    
    public void testCombination15_EmptyCartRejection() {
        OrderManagement.OrderResponse res = engine.processOrder(cart, "");
        assertFalse(res.isProcessed);
        assertEquals("Order rejected: Shopping cart is empty.", res.statusMessage);
    }

   
    public void testCombination16_CategoryDiscountClothing() {
        cart.add(new OrderManagement.OrderItem("PROD-002", "CLOTHING", 1, 100.0, 0.0, 0.0));
        OrderManagement.OrderResponse res = engine.processOrder(cart, "");
        assertEquals(10.0, res.categoryDiscount); // 10% variant configuration
    }

    
    public void testCombination17_CategoryDiscountElectronics() {
        cart.add(new OrderManagement.OrderItem("PROD-001", "ELECTRONICS", 1, 100.0, 0.0, 0.0));
        OrderManagement.OrderResponse res = engine.processOrder(cart, "");
        assertEquals(5.0, res.categoryDiscount); // 5% variant configuration
    }

   
    public void testCombination18_NullCouponHandling() {
        cart.add(new OrderManagement.OrderItem("PROD-001", "ELECTRONICS", 1, 10.0, 0.0, 0.0));
        OrderManagement.OrderResponse res = engine.processOrder(cart, null);
        assertTrue(res.isProcessed);
        assertEquals(0.0, res.couponDiscount);
    }

   
    public void testCombination19_WholesalePricingFulfillmentCalculation() {
        cart.add(new OrderManagement.OrderItem("PROD-002", "CLOTHING", 20, 100.0, 0.0, 0.0));
        OrderManagement.OrderResponse res = engine.processOrder(cart, "SUPER50");
        // Mixed deductions apply, validating boundary balance does not collapse into a negative value
        assertTrue(res.finalAmount >= 0.0);
    }

   
    public void testCombination20_ExtremeValuePriceAccuracy() {
        cart.add(new OrderManagement.OrderItem("PROD-001", "ELECTRONICS", 1, 10000.0, 0.0, 18.0));
        OrderManagement.OrderResponse res = engine.processOrder(cart, "");
        assertTrue(res.isProcessed);
        assertTrue(res.finalAmount > res.subtotal - res.totalDiscount);
    }
}
