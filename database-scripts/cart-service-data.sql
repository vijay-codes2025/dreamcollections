-- Cart Service Database Sample Data
-- Run this script against the cart-service database

-- Clear existing data (optional - be careful in production)
-- DELETE FROM cart_items;
-- DELETE FROM carts;

-- Insert sample carts for users
-- Note: user_id corresponds to the users from identity-service
INSERT INTO carts (id, user_id, created_at, updated_at) VALUES
(1, 3, '2024-01-15 10:00:00', '2024-01-15 10:30:00'),  -- vijay
(2, 4, '2024-01-16 14:20:00', '2024-01-16 14:45:00'),  -- sarah_johnson
(3, 5, '2024-01-17 09:15:00', '2024-01-17 09:40:00'),  -- mike_chen
(4, 6, '2024-01-18 16:30:00', '2024-01-18 16:55:00'),  -- emma_davis
(5, 7, '2024-01-19 11:10:00', '2024-01-19 11:35:00'),  -- john_smith
(6, 8, '2024-01-20 13:25:00', '2024-01-20 13:50:00'),  -- lisa_brown
(7, 9, '2024-01-21 15:40:00', '2024-01-21 16:05:00');  -- david_wilson

-- Insert sample cart items
-- Note: product_variant_id corresponds to the product_variants from product-catalog-service
INSERT INTO cart_items (id, cart_id, product_variant_id, quantity) VALUES
-- Vijay's cart (cart_id = 1) - Mixed adult jewelry
(1, 1, 2, 1),   -- Diamond Pendant Necklace (18 inches)
(2, 1, 17, 1),  -- Rose Gold Band Ring (Size 7)
(3, 1, 25, 1),  -- Diamond Stud Earrings (One Size)

-- Sarah's cart (cart_id = 2) - Women's jewelry
(4, 2, 5, 1),   -- Pearl Strand Necklace (18 inches)
(5, 2, 26, 2),  -- Pearl Drop Earrings (One Size)
(6, 2, 32, 1),  -- Gold Chain Bracelet (Medium)

-- Mike's cart (cart_id = 3) - Men's jewelry
(7, 3, 36, 1),  -- Men's Signet Ring (Size 10)
(8, 3, 45, 1),  -- Men's Gold Chain (20 inches)

-- Emma's cart (cart_id = 4) - Wedding jewelry
(9, 4, 83, 1),  -- Classic Engagement Ring (Size 7)
(10, 4, 91, 1), -- His & Hers Wedding Set (Size 7)

-- John's cart (cart_id = 5) - Mixed jewelry
(11, 5, 8, 1),  -- Gold Chain Necklace (18 inches)
(12, 5, 27, 1), -- Gold Hoop Earrings (One Size)
(13, 5, 31, 2), -- Gold Chain Bracelet (Small) - quantity 2

-- Lisa's cart (cart_id = 6) - Kids jewelry for her children
(14, 6, 53, 1), -- Rainbow Butterfly Necklace (14 inches)
(15, 6, 56, 1), -- Unicorn Charm Necklace (14 inches)
(16, 6, 62, 2), -- Friendship Bracelet Set (Small) - quantity 2
(17, 6, 68, 1), -- Flower Stud Earrings (Purple)
(18, 6, 75, 1), -- Adjustable Flower Ring (Purple)

-- David's cart (cart_id = 7) - Baby jewelry and kids items
(19, 7, 78, 1), -- Baby's First Bracelet (Newborn)
(20, 7, 80, 1), -- Teething Necklace (One Size)
(21, 7, 73, 1), -- Animal Stud Collection (Set of 3)
(22, 7, 77, 1); -- Mood Ring for Kids (One Size)

-- Verify the data
SELECT
    c.id as cart_id,
    c.user_id,
    ci.product_variant_id,
    ci.quantity,
    c.created_at
FROM carts c
LEFT JOIN cart_items ci ON c.id = ci.cart_id
ORDER BY c.user_id, ci.id;
