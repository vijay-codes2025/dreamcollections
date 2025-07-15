@echo off
set PGPASSWORD=postgres
echo Checking Product Catalog table structure...
"C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -h localhost -d dreamcollections_catalog -c "\d products"
pause
