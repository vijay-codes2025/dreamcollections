@echo off
echo ========================================
echo Reloading All DreamCollections Data
echo ========================================
echo.

set PGPASSWORD=postgres
set PSQL_PATH="C:\Program Files\PostgreSQL\17\bin\psql.exe"

echo [1/2] Reloading Identity Service data...
%PSQL_PATH% -U postgres -h localhost -d dreamcollections_identity -f identity-service-data.sql
if %errorlevel% neq 0 (
    echo ERROR: Failed to reload identity service data
    pause
    exit /b 1
)
echo ✓ Identity Service data reloaded successfully
echo.

echo [2/2] Reloading Product Catalog data...
%PSQL_PATH% -U postgres -h localhost -d dreamcollections_catalog -f clear-and-load-products.sql
if %errorlevel% neq 0 (
    echo ERROR: Failed to reload product catalog data
    pause
    exit /b 1
)
echo ✓ Product Catalog data reloaded successfully
echo.

echo ========================================
echo ✅ ALL DATA RELOADED SUCCESSFULLY!
echo ========================================
echo.
echo Your DreamCollections store now has:
echo • 12 users with Indian names
echo • 22 categories including Kids Collection
echo • 33 products with Indian Rupee pricing
echo • 103 product variants with stock levels
echo.
echo Kids Collection products:
echo • Rainbow Butterfly Necklace (₹2,082.50)
echo • Unicorn Charm Necklace (₹1,665.50)
echo • Friendship Bracelet Set (₹1,332.50)
echo • Flower Stud Earrings (₹1,582.50)
echo • Baby's First Bracelet (₹3,332.50)
echo • And 8 more kids products!
echo.
echo You can now access the frontend at http://localhost:5175
echo Login with: admin / password123 or vijay / password123
echo.
pause
