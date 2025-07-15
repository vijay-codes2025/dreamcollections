# DreamCollections Database Sample Data

This directory contains SQL scripts to populate the DreamCollections microservices databases with realistic sample data.

## 📋 Database Scripts Overview

### 1. **identity-service-data.sql**
- **Database**: Identity Service Database
- **Tables**: `users`
- **Data**: 12 sample users (2 admins, 10 customers)
- **Password**: All users have password `password123` (BCrypt hashed)

### 2. **product-catalog-service-data.sql**
- **Database**: Product Catalog Service Database
- **Tables**: `categories`, `products`, `product_variants`
- **Data**: 
  - 22 categories (hierarchical structure)
  - 33 products across all categories
  - 103 product variants with different sizes and stock levels

### 3. **cart-service-data.sql**
- **Database**: Cart Service Database
- **Tables**: `carts`, `cart_items`
- **Data**: 7 sample carts with various items

### 4. **order-service-data.sql**
- **Database**: Order Service Database
- **Tables**: `orders`, `order_items`
- **Data**: 5 sample orders with different statuses

## 🏷️ Product Categories Structure

```
├── Women's Jewelry
│   ├── Necklaces
│   ├── Rings
│   ├── Earrings
│   └── Bracelets
├── Men's Jewelry
│   ├── Men's Rings
│   ├── Men's Chains
│   └── Cufflinks
├── Kids Collection ⭐ NEW
│   ├── Kids Necklaces
│   ├── Kids Bracelets
│   ├── Kids Earrings
│   ├── Kids Rings
│   └── Baby Jewelry
├── Luxury Collection
│   ├── Diamond Collection
│   ├── Gold Collection
│   └── Platinum Collection
└── Wedding Collection
    ├── Engagement Rings
    └── Wedding Bands
```

## 🧒 Kids Collection Highlights

The Kids Collection includes safe, fun, and age-appropriate jewelry:

- **Safety First**: All items use hypoallergenic materials
- **Age-Appropriate**: Designed for children and babies
- **Fun Designs**: Colorful themes like butterflies, unicorns, animals
- **Adjustable Sizing**: Most items are adjustable or come in kid-friendly sizes
- **Affordable**: Priced between $12.99 - $39.99

### Sample Kids Products:
- Rainbow Butterfly Necklace ($24.99)
- Unicorn Charm Necklace ($19.99)
- Friendship Bracelet Set ($15.99)
- Flower Stud Earrings ($18.99)
- Mood Ring for Kids ($16.99)
- Baby's First Bracelet ($39.99)
- Teething Necklace ($14.99)

## 🚀 How to Run the Scripts

### Prerequisites
1. All microservices should be running
2. Databases should be created and tables should exist (run the applications first to auto-create tables)

### Execution Order
Run the scripts in this order to maintain referential integrity:

```bash
# 1. Identity Service (Users first)
mysql -u root -p identity_service_db < identity-service-data.sql

# 2. Product Catalog Service (Categories and Products)
mysql -u root -p product_catalog_db < product-catalog-service-data.sql

# 3. Cart Service (Depends on users and product variants)
mysql -u root -p cart_service_db < cart-service-data.sql

# 4. Order Service (Depends on users and product variants)
mysql -u root -p order_service_db < order-service-data.sql
```

### Alternative: Using H2 Console (if using H2 database)
1. Access H2 console at `http://localhost:8081/h2-console` (for identity service)
2. Copy and paste the SQL content from each file
3. Execute the scripts

## 👥 Sample Users

| Username | Email | Role | Password |
|----------|-------|------|----------|
| admin | admin@dreamcollections.com | ADMIN | password123 |
| manager | manager@dreamcollections.com | ADMIN | password123 |
| vijay | vijay@example.com | CUSTOMER | password123 |
| sarah_johnson | sarah.johnson@email.com | CUSTOMER | password123 |
| mike_chen | mike.chen@email.com | CUSTOMER | password123 |
| emma_davis | emma.davis@email.com | CUSTOMER | password123 |
| john_smith | john.smith@email.com | CUSTOMER | password123 |
| lisa_brown | lisa.brown@email.com | CUSTOMER | password123 |
| david_wilson | david.wilson@email.com | CUSTOMER | password123 |
| anna_garcia | anna.garcia@email.com | CUSTOMER | password123 |
| robert_taylor | robert.taylor@email.com | CUSTOMER | password123 |
| jennifer_lee | jennifer.lee@email.com | CUSTOMER | password123 |

## 🛒 Sample Cart Data

- **Vijay**: Adult jewelry mix (necklace, ring, earrings)
- **Sarah**: Women's jewelry (pearls and gold)
- **Mike**: Men's jewelry (signet ring, gold chain)
- **Emma**: Wedding jewelry (engagement ring, wedding set)
- **John**: Mixed jewelry items
- **Lisa**: Kids jewelry for her children
- **David**: Baby jewelry and kids items

## 📦 Sample Order Data

- **Order 1**: Vijay's completed order ($1,229.97) - DELIVERED
- **Order 2**: Sarah's processing order ($509.97) - PROCESSING
- **Order 3**: Mike's shipped order ($799.98) - SHIPPED
- **Order 4**: Emma's pending engagement order ($8,299.98) - PENDING
- **Order 5**: Lisa's kids jewelry order ($109.94) - DELIVERED

## 🔧 Troubleshooting

### Common Issues:
1. **Foreign Key Constraints**: Make sure to run scripts in the correct order
2. **Duplicate Keys**: Clear existing data before running scripts if needed
3. **Database Connection**: Ensure all microservices are running and databases are accessible

### Verification Queries:
Each script includes verification queries at the end to check if data was inserted correctly.

## 🎯 Next Steps

After running these scripts:
1. Start all microservices
2. Access the frontend at `http://localhost:5175`
3. Login with any of the sample users
4. Browse the categories and products
5. Test the shopping cart and checkout functionality

The sample data provides a realistic jewelry store experience with a diverse product catalog including the new Kids Collection!
