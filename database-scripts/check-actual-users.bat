@echo off
set PGPASSWORD=postgres
"C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -h localhost -d dreamcollections_identity -f check-actual-users.sql
pause
