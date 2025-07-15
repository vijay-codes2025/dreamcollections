@echo off
echo ========================================
echo DreamCollections Database Setup Script
echo ========================================
echo.
echo This script will populate all microservice databases with sample data
echo including the new Kids Collection with Indian localities and Rupee currency.
echo.

REM Check if MySQL is available
mysql --version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: MySQL is not installed or not in PATH
    echo Please install MySQL and add it to your PATH environment variable
    pause
    exit /b 1
)

echo MySQL found. Proceeding with database setup...
echo.

REM Prompt for MySQL credentials
set /p MYSQL_USER="Enter MySQL username (default: root): "
if "%MYSQL_USER%"=="" set MYSQL_USER=root

set /p MYSQL_PASSWORD="Enter MySQL password: "

echo.
echo Starting database population...
echo.

REM 1. Identity Service Database
echo [1/4] Populating Identity Service Database...
echo Running identity-service-data.sql...
mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% identity_service_db < identity-service-data.sql
if %errorlevel% neq 0 (
    echo ERROR: Failed to populate identity service database
    pause
    exit /b 1
)
echo ✓ Identity Service data loaded successfully
echo.

REM 2. Product Catalog Service Database
echo [2/4] Populating Product Catalog Service Database...
echo Running product-catalog-service-data.sql...
mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% product_catalog_db < product-catalog-service-data.sql
if %errorlevel% neq 0 (
    echo ERROR: Failed to populate product catalog database
    pause
    exit /b 1
)
echo ✓ Product Catalog data loaded successfully
echo.

REM 3. Cart Service Database
echo [3/4] Populating Cart Service Database...
echo Running cart-service-data.sql...
mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% cart_service_db < cart-service-data.sql
if %errorlevel% neq 0 (
    echo ERROR: Failed to populate cart service database
    pause
    exit /b 1
)
echo ✓ Cart Service data loaded successfully
echo.

REM 4. Order Service Database
echo [4/4] Populating Order Service Database...
echo Running order-service-data.sql...
mysql -u %MYSQL_USER% -p%MYSQL_PASSWORD% order_service_db < order-service-data.sql
if %errorlevel% neq 0 (
    echo ERROR: Failed to populate order service database
    pause
    exit /b 1
)
echo ✓ Order Service data loaded successfully
echo.

echo ========================================
echo ✅ ALL DATABASES POPULATED SUCCESSFULLY!
echo ========================================
echo.
echo Sample data includes:
echo • 12 users (2 admins, 10 customers with Indian names)
echo • 22 categories including Kids Collection
echo • 33 products with Indian Rupee pricing
echo • 103 product variants with stock levels
echo • 7 sample shopping carts
echo • 5 sample orders with Indian addresses
echo.
echo You can now:
echo 1. Start all microservices
echo 2. Access the frontend at http://localhost:5175
echo 3. Login with any user (password: password123)
echo 4. Browse the Kids Collection and other categories
echo.
echo Sample login credentials:
echo • Admin: admin / password123
echo • Customer: vijay / password123
echo • Customer: priya_sharma / password123
echo.
pause
