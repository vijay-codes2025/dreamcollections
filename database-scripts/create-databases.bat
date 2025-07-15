@echo off
echo Creating PostgreSQL databases for DreamCollections microservices...
echo.

set PGPASSWORD=postgres
set PSQL_PATH="C:\Program Files\PostgreSQL\17\bin\psql.exe"

echo Creating dreamcollections_identity database...
%PSQL_PATH% -U postgres -h localhost -c "CREATE DATABASE dreamcollections_identity;"

echo Creating dreamcollections_catalog database...
%PSQL_PATH% -U postgres -h localhost -c "CREATE DATABASE dreamcollections_catalog;"

echo Creating dreamcollections_cart database...
%PSQL_PATH% -U postgres -h localhost -c "CREATE DATABASE dreamcollections_cart;"

echo Creating dreamcollections_order database...
%PSQL_PATH% -U postgres -h localhost -c "CREATE DATABASE dreamcollections_order;"

echo.
echo Listing all databases:
%PSQL_PATH% -U postgres -h localhost -c "\l"

echo.
echo Database creation completed!
pause
