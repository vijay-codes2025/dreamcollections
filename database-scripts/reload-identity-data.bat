@echo off
set PGPASSWORD=postgres
echo Reloading identity service data...
"C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -h localhost -d dreamcollections_identity -f identity-service-data.sql
echo Done!
