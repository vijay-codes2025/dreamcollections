-- Order Service Database Sample Data
-- Run this script against the order-service database

-- Clear existing data (optional - be careful in production)
-- DELETE FROM order_items;
-- DELETE FROM orders;

-- Insert sample orders (Indian localities and Rupee currency)
INSERT INTO orders (
    id, user_id, total_amount, status, created_at, updated_at,
    shipping_street, shipping_address_line2, shipping_city, shipping_state_province,
    shipping_postal_code, shipping_country, shipping_contact_phone,
    billing_street, billing_city, billing_state_province, billing_postal_code,
    billing_country, billing_contact_phone,
    payment_transaction_id, customer_email, customer_name_snapshot,
    payment_method, payment_status, shipping_method, tracking_number
) VALUES
-- Order 1 - Vijay's completed order
(1, 3, 102497.50, 'DELIVERED', '2024-01-10 14:30:00', '2024-01-15 10:00:00',
 '123 MG Road', 'Flat 4B, Prestige Apartments', 'Bangalore', 'Karnataka', '560001', 'India', '+91-9876543210',
 '123 MG Road', 'Bangalore', 'Karnataka', '560001', 'India', '+91-9876543210',
 'razorpay_1234567890', 'vijay@example.com', 'Vijay Kumar',
 'UPI', 'PAID', 'Standard Delivery', 'DTDC123456789'),

-- Order 2 - Priya's processing order
(2, 4, 42497.50, 'PROCESSING', '2024-01-18 09:15:00', '2024-01-18 16:30:00',
 '456 Linking Road', 'Bandra West', 'Mumbai', 'Maharashtra', '400050', 'India', '+91-9876543456',
 '456 Linking Road', 'Mumbai', 'Maharashtra', '400050', 'India', '+91-9876543456',
 'paytm_2345678901', 'priya.sharma@gmail.com', 'Priya Sharma',
 'Credit Card', 'PAID', 'Express Delivery', NULL),

-- Order 3 - Rahul's shipped order
(3, 5, 66648.50, 'SHIPPED', '2024-01-20 11:45:00', '2024-01-22 14:20:00',
 '789 Park Street', 'Near Victoria Memorial', 'Kolkata', 'West Bengal', '700016', 'India', '+91-9876543789',
 '789 Park Street', 'Kolkata', 'West Bengal', '700016', 'India', '+91-9876543789',
 'phonepe_3456789012', 'rahul.patel@yahoo.com', 'Rahul Patel',
 'Net Banking', 'PAID', 'Standard Delivery', 'BLUEDART987654321'),

-- Order 4 - Anita's pending order
(4, 6, 691648.50, 'PENDING', '2024-01-22 16:00:00', '2024-01-22 16:00:00',
 '321 Anna Salai', 'T Nagar', 'Chennai', 'Tamil Nadu', '600017', 'India', '+91-9876543321',
 '321 Anna Salai', 'Chennai', 'Tamil Nadu', '600017', 'India', '+91-9876543321',
 'gpay_4567890123', 'anita.singh@hotmail.com', 'Anita Singh',
 'UPI', 'PENDING', 'Express Delivery', NULL),

-- Order 5 - Kavya's completed kids jewelry order
(5, 8, 9162.50, 'DELIVERED', '2024-01-12 13:20:00', '2024-01-17 09:30:00',
 '654 Sector 17', 'Chandigarh Housing Board', 'Chandigarh', 'Chandigarh', '160017', 'India', '+91-9876543654',
 '654 Sector 17', 'Chandigarh', 'Chandigarh', '160017', 'India', '+91-9876543654',
 'paytm_5678901234', 'kavya.nair@outlook.com', 'Kavya Nair',
 'Debit Card', 'PAID', 'Standard Delivery', 'ECOM456789123');

-- Insert order items (prices in Indian Rupees)
INSERT INTO order_items (
    id, order_id, product_variant_id, product_name, variant_size,
    product_image_url, quantity, price_at_purchase, total_price
) VALUES
-- Order 1 items (Vijay's order) - ₹102,497.50
(1, 1, 2, 'Diamond Pendant Necklace', '18 inches', 'https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=500', 1, 74999.00, 74999.00),
(2, 1, 17, 'Rose Gold Band Ring', 'Size 7', 'https://images.unsplash.com/photo-1603561596112-db1d4d4e4c3a?w=500', 1, 12499.00, 12499.00),
(3, 1, 25, 'Diamond Stud Earrings', 'One Size', 'https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?w=500', 1, 14999.50, 14999.50),

-- Order 2 items (Sarah's order) - ₹42,497.50
(4, 2, 5, 'Pearl Strand Necklace', '18 inches', 'https://images.unsplash.com/photo-1506630448388-4e683c67ddb0?w=500', 1, 24999.00, 24999.00),
(5, 2, 26, 'Pearl Drop Earrings', 'One Size', 'https://images.unsplash.com/photo-1506630448388-4e683c67ddb0?w=500', 2, 7499.50, 14999.00),
(6, 2, 32, 'Gold Chain Bracelet', 'Medium', 'https://images.unsplash.com/photo-1611652022419-a9419f74343d?w=500', 1, 7499.50, 7499.50),

-- Order 3 items (Mike's order) - ₹66,648.50
(7, 3, 36, 'Men''s Signet Ring', 'Size 10', 'https://images.unsplash.com/photo-1603561596112-db1d4d4e4c3a?w=500', 1, 16649.50, 16649.50),
(8, 3, 45, 'Men''s Gold Chain', '20 inches', 'https://images.unsplash.com/photo-1611652022419-a9419f74343d?w=500', 1, 49999.00, 49999.00),

-- Order 4 items (Emma's engagement order) - ₹6,91,648.50
(9, 4, 83, 'Classic Engagement Ring', 'Size 7', 'https://images.unsplash.com/photo-1605100804763-247f67b3557e?w=500', 1, 416649.00, 416649.00),
(10, 4, 91, 'His & Hers Wedding Set', 'Size 7', 'https://images.unsplash.com/photo-1603561596112-db1d4d4e4c3a?w=500', 1, 74999.50, 74999.50),

-- Order 5 items (Lisa's kids jewelry order) - ₹9,162.50
(11, 5, 53, 'Rainbow Butterfly Necklace', '14 inches', 'https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=500', 1, 2082.50, 2082.50),
(12, 5, 56, 'Unicorn Charm Necklace', '14 inches', 'https://images.unsplash.com/photo-1611652022419-a9419f74343d?w=500', 1, 1665.50, 1665.50),
(13, 5, 62, 'Friendship Bracelet Set', 'Small (5-6")', 'https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=500', 2, 1332.50, 2665.00),
(14, 5, 68, 'Flower Stud Earrings', 'Purple', 'https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?w=500', 1, 1582.50, 1582.50),
(15, 5, 75, 'Adjustable Flower Ring', 'Purple', 'https://images.unsplash.com/photo-1603561596112-db1d4d4e4c3a?w=500', 1, 1082.50, 1082.50);

-- Verify the data
SELECT 
    o.id as order_id,
    o.user_id,
    o.customer_name_snapshot,
    o.total_amount,
    o.status,
    o.payment_status,
    oi.product_name,
    oi.variant_size,
    oi.quantity,
    oi.price_at_purchase
FROM orders o
LEFT JOIN order_items oi ON o.id = oi.order_id
ORDER BY o.id, oi.id;
