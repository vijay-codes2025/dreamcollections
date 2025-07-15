-- Check what users actually exist in the database
SELECT id, username, email, phone_number, first_name, last_name, role 
FROM users 
ORDER BY id;
