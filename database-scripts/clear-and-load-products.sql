-- Clear existing data and load fresh product catalog data
-- Run this script against the PostgreSQL dreamcollections_catalog database

-- Clear existing data in correct order (due to foreign keys)
DELETE FROM product_variants;
DELETE FROM products;
DELETE FROM categories;

-- Reset sequences
ALTER SEQUENCE categories_id_seq RESTART WITH 1;
ALTER SEQUENCE products_id_seq RESTART WITH 1;
ALTER SEQUENCE product_variants_id_seq RESTART WITH 1;

-- Insert Categories
INSERT INTO categories (id, name, description, parent_category_id) VALUES
-- Top-level categories
(1, 'Women''s Jewelry', 'Elegant jewelry collection designed for women', NULL),
(2, 'Men''s Jewelry', 'Sophisticated jewelry pieces for men', NULL),
(3, 'Kids Collection', 'Safe and fun jewelry designed for children', NULL),
(4, 'Luxury Collection', 'Premium and luxury jewelry pieces', NULL),
(5, 'Wedding Collection', 'Special jewelry for weddings', NULL),

-- Women's Jewelry subcategories
(6, 'Necklaces', 'Beautiful necklaces in various styles', 1),
(7, 'Rings', 'Elegant rings for every occasion', 1),
(8, 'Earrings', 'Stunning earrings from studs to statement pieces', 1),
(9, 'Bracelets', 'Delicate and bold bracelets', 1),

-- Men's Jewelry subcategories
(10, 'Men''s Rings', 'Masculine rings in various metals', 2),
(11, 'Men''s Chains', 'Strong and stylish chains for men', 2),
(12, 'Cufflinks', 'Elegant cufflinks for formal occasions', 2),

-- Kids Collection subcategories
(13, 'Kids Necklaces', 'Safe and colorful necklaces for children', 3),
(14, 'Kids Bracelets', 'Fun and adjustable bracelets for kids', 3),
(15, 'Kids Earrings', 'Hypoallergenic earrings for sensitive ears', 3),
(16, 'Kids Rings', 'Adjustable rings with fun designs', 3),
(17, 'Baby Jewelry', 'Gentle jewelry pieces for babies', 3),

-- Luxury Collection subcategories
(18, 'Diamond Collection', 'Exquisite diamond jewelry pieces', 4),
(19, 'Gold Collection', 'Premium gold jewelry', 4),
(20, 'Platinum Collection', 'Luxury platinum jewelry', 4),

-- Wedding Collection subcategories
(21, 'Engagement Rings', 'Beautiful engagement rings', 5),
(22, 'Wedding Bands', 'Perfect wedding bands for couples', 5);

-- Insert Products (without description field due to OID type)
INSERT INTO products (id, name, price, image_url, video_url, category_id) VALUES
-- Women's Necklaces
(1, 'Diamond Pendant Necklace', 74999.00, 'https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=500', NULL, 6),
(2, 'Pearl Strand Necklace', 24999.00, 'https://images.unsplash.com/photo-1506630448388-4e683c67ddb0?w=500', NULL, 6),
(3, 'Gold Chain Necklace', 16649.50, 'https://images.unsplash.com/photo-1611652022419-a9419f74343d?w=500', NULL, 6),

-- Women's Rings
(4, 'Solitaire Diamond Ring', 208299.00, 'https://images.unsplash.com/photo-1605100804763-247f67b3557e?w=500', NULL, 7),
(5, 'Rose Gold Band Ring', 12499.00, 'https://images.unsplash.com/photo-1603561596112-db1d4d4e4c3a?w=500', NULL, 7),
(6, 'Vintage Style Ring', 49999.00, 'https://images.unsplash.com/photo-1584302179602-e4578d0bf329?w=500', NULL, 7),

-- Women's Earrings
(7, 'Diamond Stud Earrings', 66649.00, 'https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?w=500', NULL, 8),
(8, 'Pearl Drop Earrings', 14999.50, 'https://images.unsplash.com/photo-1506630448388-4e683c67ddb0?w=500', NULL, 8),
(9, 'Gold Hoop Earrings', 10832.50, 'https://images.unsplash.com/photo-1611652022419-a9419f74343d?w=500', NULL, 8),

-- Women's Bracelets
(10, 'Tennis Bracelet', 108324.50, 'https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=500', NULL, 9),
(11, 'Gold Chain Bracelet', 7499.50, 'https://images.unsplash.com/photo-1611652022419-a9419f74343d?w=500', NULL, 9),

-- Men's Rings
(12, 'Men''s Signet Ring', 16649.50, 'https://images.unsplash.com/photo-1603561596112-db1d4d4e4c3a?w=500', NULL, 10),
(13, 'Titanium Wedding Band', 24999.00, 'https://images.unsplash.com/photo-1584302179602-e4578d0bf329?w=500', NULL, 10),

-- Men's Chains
(14, 'Men''s Gold Chain', 49999.00, 'https://images.unsplash.com/photo-1611652022419-a9419f74343d?w=500', NULL, 11),
(15, 'Silver Chain Necklace', 12499.00, 'https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=500', NULL, 11),

-- Kids Necklaces - NEW KIDS COLLECTION
(16, 'Rainbow Butterfly Necklace', 2082.50, 'https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=500', NULL, 13),
(17, 'Unicorn Charm Necklace', 1665.50, 'https://images.unsplash.com/photo-1611652022419-a9419f74343d?w=500', NULL, 13),
(18, 'Heart Locket Necklace', 2499.00, 'https://images.unsplash.com/photo-1506630448388-4e683c67ddb0?w=500', NULL, 13),

-- Kids Bracelets
(19, 'Friendship Bracelet Set', 1332.50, 'https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=500', NULL, 14),
(20, 'Charm Bracelet Starter', 2915.50, 'https://images.unsplash.com/photo-1611652022419-a9419f74343d?w=500', NULL, 14),
(21, 'Beaded Name Bracelet', 1915.50, 'https://images.unsplash.com/photo-1506630448388-4e683c67ddb0?w=500', NULL, 14),

-- Kids Earrings
(22, 'Flower Stud Earrings', 1582.50, 'https://images.unsplash.com/photo-1535632066927-ab7c9ab60908?w=500', NULL, 15),
(23, 'Star Drop Earrings', 1832.50, 'https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=500', NULL, 15),
(24, 'Animal Stud Collection', 2249.00, 'https://images.unsplash.com/photo-1611652022419-a9419f74343d?w=500', NULL, 15),

-- Kids Rings
(25, 'Adjustable Flower Ring', 1082.50, 'https://images.unsplash.com/photo-1603561596112-db1d4d4e4c3a?w=500', NULL, 16),
(26, 'Mood Ring for Kids', 1415.50, 'https://images.unsplash.com/photo-1584302179602-e4578d0bf329?w=500', NULL, 16),

-- Baby Jewelry
(27, 'Baby''s First Bracelet', 3332.50, 'https://images.unsplash.com/photo-1506630448388-4e683c67ddb0?w=500', NULL, 17),
(28, 'Teething Necklace', 1249.00, 'https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=500', NULL, 17),

-- Engagement Rings
(29, 'Classic Engagement Ring', 416649.00, 'https://images.unsplash.com/photo-1605100804763-247f67b3557e?w=500', NULL, 21),
(30, 'Vintage Engagement Ring', 274999.00, 'https://images.unsplash.com/photo-1584302179602-e4578d0bf329?w=500', NULL, 21),

-- Wedding Bands
(31, 'His & Hers Wedding Set', 74999.50, 'https://images.unsplash.com/photo-1603561596112-db1d4d4e4c3a?w=500', NULL, 22),
(32, 'Diamond Wedding Band', 99999.00, 'https://images.unsplash.com/photo-1605100804763-247f67b3557e?w=500', NULL, 22),

-- Luxury Collection
(33, 'Luxury Diamond Necklace', 1333249.00, 'https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=500', NULL, 18);

-- Insert Product Variants
INSERT INTO product_variants (id, product_id, size, stock_quantity) VALUES
-- Women's Necklaces
(1, 1, '16 inches', 15), (2, 1, '18 inches', 25), (3, 1, '20 inches', 10),
(4, 2, '16 inches', 20), (5, 2, '18 inches', 30), (6, 2, '20 inches', 15),
(7, 3, '16 inches', 40), (8, 3, '18 inches', 50), (9, 3, '20 inches', 25),

-- Women's Rings
(10, 4, 'Size 5', 3), (11, 4, 'Size 6', 5), (12, 4, 'Size 7', 8), (13, 4, 'Size 8', 5), (14, 4, 'Size 9', 2),
(15, 5, 'Size 5', 12), (16, 5, 'Size 6', 15), (17, 5, 'Size 7', 20), (18, 5, 'Size 8', 15), (19, 5, 'Size 9', 8),
(20, 6, 'Size 5', 8), (21, 6, 'Size 6', 10), (22, 6, 'Size 7', 12), (23, 6, 'Size 8', 10), (24, 6, 'Size 9', 5),

-- Women's Earrings
(25, 7, 'One Size', 30), (26, 8, 'One Size', 25), (27, 9, 'One Size', 40),

-- Women's Bracelets
(28, 10, 'Small', 8), (29, 10, 'Medium', 12), (30, 10, 'Large', 6),
(31, 11, 'Small', 20), (32, 11, 'Medium', 25), (33, 11, 'Large', 15),

-- Men's Rings
(34, 12, 'Size 8', 10), (35, 12, 'Size 9', 15), (36, 12, 'Size 10', 20), (37, 12, 'Size 11', 12), (38, 12, 'Size 12', 8),
(39, 13, 'Size 8', 5), (40, 13, 'Size 9', 8), (41, 13, 'Size 10', 12), (42, 13, 'Size 11', 8), (43, 13, 'Size 12', 5),

-- Men's Chains
(44, 14, '18 inches', 8), (45, 14, '20 inches', 12), (46, 14, '22 inches', 10), (47, 14, '24 inches', 6),
(48, 15, '18 inches', 15), (49, 15, '20 inches', 20), (50, 15, '22 inches', 15), (51, 15, '24 inches', 10),

-- Kids Necklaces - KIDS COLLECTION
(52, 16, '12 inches', 25), (53, 16, '14 inches', 30), (54, 16, '16 inches', 20),
(55, 17, '12 inches', 35), (56, 17, '14 inches', 40), (57, 17, '16 inches', 25),
(58, 18, '12 inches', 20), (59, 18, '14 inches', 25), (60, 18, '16 inches', 15),

-- Kids Bracelets
(61, 19, 'Small', 50), (62, 19, 'Medium', 45),
(63, 20, 'Small', 30), (64, 20, 'Medium', 35),
(65, 21, 'Small', 40), (66, 21, 'Medium', 35),

-- Kids Earrings
(67, 22, 'Pink', 25), (68, 22, 'Purple', 30), (69, 22, 'Blue', 20), (70, 22, 'Yellow', 15),
(71, 23, 'Silver', 35), (72, 23, 'Gold', 30), (73, 24, 'Set of 3', 40),

-- Kids Rings
(74, 25, 'Pink', 30), (75, 25, 'Purple', 25), (76, 25, 'Blue', 20), (77, 26, 'One Size', 45),

-- Baby Jewelry
(78, 27, 'Newborn', 15), (79, 27, 'Infant', 20), (80, 28, 'One Size', 50),

-- Engagement Rings
(81, 29, 'Size 5', 2), (82, 29, 'Size 6', 3), (83, 29, 'Size 7', 4), (84, 29, 'Size 8', 3),
(85, 30, 'Size 5', 3), (86, 30, 'Size 6', 4), (87, 30, 'Size 7', 5), (88, 30, 'Size 8', 3),

-- Wedding Bands
(89, 31, 'Size 5', 5), (90, 31, 'Size 6', 8), (91, 31, 'Size 7', 10), (92, 31, 'Size 8', 8), (93, 31, 'Size 9', 6),
(94, 31, 'Size 10', 8), (95, 31, 'Size 11', 5), (96, 31, 'Size 12', 3),
(97, 32, 'Size 5', 4), (98, 32, 'Size 6', 6), (99, 32, 'Size 7', 8), (100, 32, 'Size 8', 6), (101, 32, 'Size 9', 4),

-- Luxury Collection
(102, 33, '18 inches', 2), (103, 33, '20 inches', 1);
