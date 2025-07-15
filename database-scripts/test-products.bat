@echo off
set PGPASSWORD=postgres
echo Loading Product Catalog data...
"C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -h localhost -d dreamcollections_catalog -f product-catalog-simple.sql
echo Product Catalog data loading completed.
pause
