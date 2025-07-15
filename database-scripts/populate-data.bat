@echo off
echo ========================================
echo Populating DreamCollections Database
echo ========================================
echo.

set PGPASSWORD=postgres
set PSQL_PATH="C:\Program Files\PostgreSQL\17\bin\psql.exe"

echo [1/4] Populating Identity Service Database...
%PSQL_PATH% -U postgres -h localhost -d dreamcollections_identity -f identity-service-data.sql
if %errorlevel% neq 0 (
    echo ERROR: Failed to populate identity service database
    pause
    exit /b 1
)
echo ✓ Identity Service data loaded successfully
echo.

echo [2/4] Populating Product Catalog Service Database...
%PSQL_PATH% -U postgres -h localhost -d dreamcollections_catalog -f product-catalog-service-data.sql
if %errorlevel% neq 0 (
    echo ERROR: Failed to populate product catalog database
    pause
    exit /b 1
)
echo ✓ Product Catalog data loaded successfully
echo.

echo [3/4] Populating Cart Service Database...
%PSQL_PATH% -U postgres -h localhost -d dreamcollections_cart -f cart-service-data.sql
if %errorlevel% neq 0 (
    echo ERROR: Failed to populate cart service database
    pause
    exit /b 1
)
echo ✓ Cart Service data loaded successfully
echo.

echo [4/4] Populating Order Service Database...
%PSQL_PATH% -U postgres -h localhost -d dreamcollections_order -f order-service-data.sql
if %errorlevel% neq 0 (
    echo ERROR: Failed to populate order service database
    pause
    exit /b 1
)
echo ✓ Order Service data loaded successfully
echo.

echo ========================================
echo ✅ ALL DATA POPULATED SUCCESSFULLY!
echo ========================================
echo.
echo Sample data includes:
echo • 12 users with Indian names
echo • Kids Collection with 13 products
echo • Indian Rupee pricing
echo • Indian localities and addresses
echo • 33 total products across all categories
echo • 103 product variants with stock levels
echo.
pause
