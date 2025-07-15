@echo off
set PGPASSWORD=postgres
echo Updating existing users with phone numbers...
"C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -h localhost -d dreamcollections_identity -f update-existing-users.sql
echo.
echo ✅ Users updated successfully!
pause
