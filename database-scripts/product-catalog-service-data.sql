-- Product Catalog Service Database Sample Data (PostgreSQL)
-- Run this script against the PostgreSQL dreamcollections database

-- Clear existing data (optional - be careful in production)
-- DELETE FROM product_variants;
-- DELETE FROM products;
-- DELETE FROM categories;

-- Insert Categories (Hierarchical structure)
INSERT INTO categories (id, name, description, parent_category_id) VALUES
-- Top-level categories
(1, 'Women''s Jewelry', 'Elegant jewelry collection designed for women including rings, necklaces, earrings, and bracelets', NULL),
(2, 'Men''s Jewelry', 'Sophisticated jewelry pieces for men including watches, rings, and chains', NULL),
(3, 'Kids Collection', 'Safe and fun jewelry designed specifically for children with hypoallergenic materials', NULL),
(4, 'Luxury Collection', 'Premium and luxury jewelry pieces with precious stones and metals', NULL),
(5, 'Wedding Collection', 'Special jewelry for weddings including engagement rings and wedding bands', NULL),

-- Women's Jewelry subcategories
(6, 'Necklaces', 'Beautiful necklaces in various styles and materials', 1),
(7, 'Rings', 'Elegant rings for every occasion', 1),
(8, 'Earrings', 'Stunning earrings from studs to statement pieces', 1),
(9, 'Bracelets', 'Delicate and bold bracelets for every style', 1),

-- Men's Jewelry subcategories
(10, 'Men''s Rings', 'Masculine rings in various metals and styles', 2),
(11, 'Men''s Chains', 'Strong and stylish chains for men', 2),
(12, 'Cufflinks', 'Elegant cufflinks for formal occasions', 2),

-- Kids Collection subcategories
(13, 'Kids Necklaces', 'Safe and colorful necklaces for children with breakaway clasps', 3),
(14, 'Kids Bracelets', 'Fun and adjustable bracelets perfect for little wrists', 3),
(15, 'Kids Earrings', 'Hypoallergenic earrings designed for sensitive young ears', 3),
(16, 'Kids Rings', 'Adjustable rings with fun designs for children', 3),
(17, 'Baby Jewelry', 'Gentle jewelry pieces perfect for babies and toddlers', 3),

-- Luxury Collection subcategories
(18, 'Diamond Collection', 'Exquisite diamond jewelry pieces', 4),
(19, 'Gold Collection', 'Premium gold jewelry', 4),
(20, 'Platinum Collection', 'Luxury platinum jewelry', 4),

-- Wedding Collection subcategories
(21, 'Engagement Rings', 'Beautiful engagement rings for proposals', 5),
(22, 'Wedding Bands', 'Perfect wedding bands for couples', 5);

-- Insert Products (using NULL for description due to OID type issue)
INSERT INTO products (id, name, price, image_url, video_url, category_id) VALUES
-- Necklaces (category_id = 6) - Prices in Indian Rupees
(1, 'Diamond Pendant Necklace', 74999.00, 'https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=500', NULL, 6),
(2, 'Pearl Strand Necklace', 24999.00, 'https://images.unsplash.com/photo-1506630448388-4e683c67ddb0?w=500', NULL, 6),
(3, 'Gold Chain Necklace', 16649.50, 'https://images.unsplash.com/photo-1611652022419-a9419f74343d?w=500', NULL, 6),

-- Rings (category_id = 7)
(4, 'Solitaire Diamond Ring', 208299.00, 'https://images.unsplash.com/photo-1605100804763-247f67b3557e?w=500', NULL, 7),
(5, 'Rose Gold Band Ring', 12499.00, 'https://images.unsplash.com/photo-1603561596112-db1d4d4e4c3a?w=500', NULL, 7),
(6, 'Vintage Style Ring', 49999.00, 'https://images.unsplash.com/photo-1584302179602-e4578d0bf329?w=500', NULL, 7),

-- Earrings (category_id = 8)
(7, 'Diamond Stud Earrings', 'Classic diamond stud earrings in 14k white gold settings.', 66649.00, 'https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?w=500', NULL, 8),
(8, 'Pearl Drop Earrings', 'Elegant pearl drop earrings with sterling silver hooks.', 14999.50, 'https://images.unsplash.com/photo-1506630448388-4e683c67ddb0?w=500', NULL, 8),
(9, 'Gold Hoop Earrings', 'Classic gold hoop earrings in 14k yellow gold.', 10832.50, 'https://images.unsplash.com/photo-1611652022419-a9419f74343d?w=500', NULL, 8),

-- Bracelets (category_id = 9)
(10, 'Tennis Bracelet', 'Stunning tennis bracelet with diamonds set in white gold.', 108324.50, 'https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=500', NULL, 9),
(11, 'Gold Chain Bracelet', 'Delicate gold chain bracelet perfect for stacking.', 7499.50, 'https://images.unsplash.com/photo-1611652022419-a9419f74343d?w=500', NULL, 9),

-- Men's Rings (category_id = 10)
(12, 'Men''s Signet Ring', 'Classic men''s signet ring in sterling silver with customizable engraving.', 16649.50, 'https://images.unsplash.com/photo-1603561596112-db1d4d4e4c3a?w=500', NULL, 10),
(13, 'Titanium Wedding Band', 'Modern titanium wedding band with brushed finish.', 24999.00, 'https://images.unsplash.com/photo-1584302179602-e4578d0bf329?w=500', NULL, 10),

-- Men's Chains (category_id = 11)
(14, 'Men''s Gold Chain', 'Heavy 14k gold chain necklace for men.', 49999.00, 'https://images.unsplash.com/photo-1611652022419-a9419f74343d?w=500', NULL, 11),
(15, 'Silver Chain Necklace', 'Sterling silver chain necklace with modern design.', 12499.00, 'https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=500', NULL, 11),

-- Kids Necklaces (category_id = 13)
(16, 'Rainbow Butterfly Necklace', 'Colorful butterfly pendant necklace with breakaway clasp for safety. Made with hypoallergenic materials.', 2082.50, 'https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=500', NULL, 13),
(17, 'Unicorn Charm Necklace', 'Magical unicorn pendant on adjustable cord necklace. Perfect for young dreamers.', 1665.50, 'https://images.unsplash.com/photo-1611652022419-a9419f74343d?w=500', NULL, 13),
(18, 'Heart Locket Necklace', 'Sweet heart-shaped locket necklace where kids can keep special photos.', 2499.00, 'https://images.unsplash.com/photo-1506630448388-4e683c67ddb0?w=500', NULL, 13),

-- Kids Bracelets (category_id = 14)
(19, 'Friendship Bracelet Set', 'Set of 3 colorful friendship bracelets with adjustable sizing. Perfect for sharing with friends.', 1332.50, 'https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=500', NULL, 14),
(20, 'Charm Bracelet Starter', 'Adjustable charm bracelet with 3 starter charms (star, heart, flower). Add more charms over time.', 2915.50, 'https://images.unsplash.com/photo-1611652022419-a9419f74343d?w=500', NULL, 14),
(21, 'Beaded Name Bracelet', 'Personalized beaded bracelet with child''s name. Available in multiple color combinations.', 1915.50, 'https://images.unsplash.com/photo-1506630448388-4e683c67ddb0?w=500', NULL, 14),

-- Kids Earrings (category_id = 15)
(22, 'Flower Stud Earrings', 'Hypoallergenic flower stud earrings perfect for sensitive ears. Available in multiple colors.', 1582.50, 'https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?w=500', NULL, 15),
(23, 'Star Drop Earrings', 'Fun star-shaped drop earrings with secure backs. Lightweight and comfortable for all-day wear.', 1832.50, 'https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=500', NULL, 15),
(24, 'Animal Stud Collection', 'Set of 3 pairs of animal stud earrings (cat, dog, bunny). Hypoallergenic and kid-safe.', 2249.00, 'https://images.unsplash.com/photo-1611652022419-a9419f74343d?w=500', NULL, 15),

-- Kids Rings (category_id = 16)
(25, 'Adjustable Flower Ring', 'Cute flower ring that adjusts to fit growing fingers. Available in pink, purple, and blue.', 1082.50, 'https://images.unsplash.com/photo-1603561596112-db1d4d4e4c3a?w=500', NULL, 16),
(26, 'Mood Ring for Kids', 'Fun mood ring that changes colors with temperature. Adjustable band fits most kid sizes.', 1415.50, 'https://images.unsplash.com/photo-1584302179602-e4578d0bf329?w=500', NULL, 16),

-- Baby Jewelry (category_id = 17)
(27, 'Baby''s First Bracelet', 'Gentle sterling silver bracelet designed for babies. Includes safety clasp and gift box.', 3332.50, 'https://images.unsplash.com/photo-1506630448388-4e683c67ddb0?w=500', NULL, 17),
(28, 'Teething Necklace', 'Safe silicone teething necklace for babies. BPA-free and easy to clean.', 1249.00, 'https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=500', NULL, 17),

-- Engagement Rings (category_id = 21)
(29, 'Classic Engagement Ring', 'Timeless engagement ring with 2 carat diamond in platinum setting.', 416649.00, 'https://images.unsplash.com/photo-1605100804763-247f67b3557e?w=500', NULL, 21),
(30, 'Vintage Engagement Ring', 'Art deco inspired engagement ring with halo setting.', 274999.00, 'https://images.unsplash.com/photo-1584302179602-e4578d0bf329?w=500', NULL, 21),

-- Wedding Bands (category_id = 22)
(31, 'His & Hers Wedding Set', 'Matching wedding band set in 14k white gold.', 74999.50, 'https://images.unsplash.com/photo-1603561596112-db1d4d4e4c3a?w=500', NULL, 22),
(32, 'Diamond Wedding Band', 'Wedding band with channel-set diamonds.', 99999.00, 'https://images.unsplash.com/photo-1605100804763-247f67b3557e?w=500', NULL, 22),

-- Luxury Collection (category_id = 18)
(33, 'Luxury Diamond Necklace', 'Exquisite diamond necklace with multiple carats in platinum.', 1333249.00, 'https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=500', NULL, 18);

-- Insert Product Variants
INSERT INTO product_variants (id, product_id, size, stock_quantity) VALUES
-- Women's Necklaces (16", 18", 20" lengths)
(1, 1, '16 inches', 15),
(2, 1, '18 inches', 25),
(3, 1, '20 inches', 10),
(4, 2, '16 inches', 20),
(5, 2, '18 inches', 30),
(6, 2, '20 inches', 15),
(7, 3, '16 inches', 40),
(8, 3, '18 inches', 50),
(9, 3, '20 inches', 25),

-- Rings (sizes 5-9)
(10, 4, 'Size 5', 3),
(11, 4, 'Size 6', 5),
(12, 4, 'Size 7', 8),
(13, 4, 'Size 8', 5),
(14, 4, 'Size 9', 2),
(15, 5, 'Size 5', 12),
(16, 5, 'Size 6', 15),
(17, 5, 'Size 7', 20),
(18, 5, 'Size 8', 15),
(19, 5, 'Size 9', 8),
(20, 6, 'Size 5', 8),
(21, 6, 'Size 6', 10),
(22, 6, 'Size 7', 12),
(23, 6, 'Size 8', 10),
(24, 6, 'Size 9', 5),

-- Earrings (One Size)
(25, 7, 'One Size', 30),
(26, 8, 'One Size', 25),
(27, 9, 'One Size', 40),

-- Bracelets (Small, Medium, Large)
(28, 10, 'Small (6.5")', 8),
(29, 10, 'Medium (7")', 12),
(30, 10, 'Large (7.5")', 6),
(31, 11, 'Small (6.5")', 20),
(32, 11, 'Medium (7")', 25),
(33, 11, 'Large (7.5")', 15),

-- Men's Rings (sizes 8-12)
(34, 12, 'Size 8', 10),
(35, 12, 'Size 9', 15),
(36, 12, 'Size 10', 20),
(37, 12, 'Size 11', 12),
(38, 12, 'Size 12', 8),
(39, 13, 'Size 8', 5),
(40, 13, 'Size 9', 8),
(41, 13, 'Size 10', 12),
(42, 13, 'Size 11', 8),
(43, 13, 'Size 12', 5),

-- Men's Chains (18", 20", 22", 24")
(44, 14, '18 inches', 8),
(45, 14, '20 inches', 12),
(46, 14, '22 inches', 10),
(47, 14, '24 inches', 6),
(48, 15, '18 inches', 15),
(49, 15, '20 inches', 20),
(50, 15, '22 inches', 15),
(51, 15, '24 inches', 10),

-- Kids Necklaces (12", 14", 16" lengths - shorter for kids)
(52, 16, '12 inches', 25),
(53, 16, '14 inches', 30),
(54, 16, '16 inches', 20),
(55, 17, '12 inches', 35),
(56, 17, '14 inches', 40),
(57, 17, '16 inches', 25),
(58, 18, '12 inches', 20),
(59, 18, '14 inches', 25),
(60, 18, '16 inches', 15),

-- Kids Bracelets (Small, Medium - adjustable)
(61, 19, 'Small (5-6")', 50),
(62, 19, 'Medium (6-7")', 45),
(63, 20, 'Small (5-6")', 30),
(64, 20, 'Medium (6-7")', 35),
(65, 21, 'Small (5-6")', 40),
(66, 21, 'Medium (6-7")', 35),

-- Kids Earrings (One Size)
(67, 22, 'Pink', 25),
(68, 22, 'Purple', 30),
(69, 22, 'Blue', 20),
(70, 22, 'Yellow', 15),
(71, 23, 'Silver', 35),
(72, 23, 'Gold', 30),
(73, 24, 'Set of 3', 40),

-- Kids Rings (Adjustable)
(74, 25, 'Pink', 30),
(75, 25, 'Purple', 25),
(76, 25, 'Blue', 20),
(77, 26, 'One Size', 45),

-- Baby Jewelry
(78, 27, 'Newborn (4")', 15),
(79, 27, 'Infant (4.5")', 20),
(80, 28, 'One Size', 50),

-- Engagement Rings (sizes 5-8)
(81, 29, 'Size 5', 2),
(82, 29, 'Size 6', 3),
(83, 29, 'Size 7', 4),
(84, 29, 'Size 8', 3),
(85, 30, 'Size 5', 3),
(86, 30, 'Size 6', 4),
(87, 30, 'Size 7', 5),
(88, 30, 'Size 8', 3),

-- Wedding Bands (sizes 5-12)
(89, 31, 'Size 5', 5),
(90, 31, 'Size 6', 8),
(91, 31, 'Size 7', 10),
(92, 31, 'Size 8', 8),
(93, 31, 'Size 9', 6),
(94, 31, 'Size 10', 8),
(95, 31, 'Size 11', 5),
(96, 31, 'Size 12', 3),
(97, 32, 'Size 5', 4),
(98, 32, 'Size 6', 6),
(99, 32, 'Size 7', 8),
(100, 32, 'Size 8', 6),
(101, 32, 'Size 9', 4),

-- Luxury Collection
(102, 33, '18 inches', 2),
(103, 33, '20 inches', 1);

-- Verify the data
SELECT
    c.name as category_name,
    p.name as product_name,
    p.price,
    pv.size,
    pv.stock_quantity
FROM categories c
JOIN products p ON c.id = p.category_id
JOIN product_variants pv ON p.id = pv.product_id
ORDER BY c.name, p.name, pv.size;
