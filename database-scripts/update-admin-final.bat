@echo off
set PGPASSWORD=postgres
echo Updating admin user...
"C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -h localhost -d dreamcollections_identity -f database-scripts\update-admin-final.sql
echo Done!
