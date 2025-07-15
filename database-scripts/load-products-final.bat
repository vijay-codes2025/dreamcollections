@echo off
set PGPASSWORD=postgres
echo Clearing and loading Product Catalog data...
"C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -h localhost -d dreamcollections_catalog -f clear-and-load-products.sql
echo Product Catalog data loading completed.
pause
