@echo off
set PGPASSWORD=postgres
echo Fixing admin user...
"C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -h localhost -d dreamcollections_identity -f fix-admin-user.sql
echo Done!
