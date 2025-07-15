@echo off
set PGPASSWORD=postgres
"C:\Program Files\PostgreSQL\17\bin\psql.exe" -U postgres -h localhost -d dreamcollections_identity -f identity-service-data.sql
echo Identity data population completed.
pause
