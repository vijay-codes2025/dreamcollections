-- Fix the admin user with proper email and phone number
UPDATE users 
SET email = 'admin@dreamcollections.com',
    phone_number = '+919876543210'
WHERE username = 'admin';

-- Show the updated admin user
SELECT id, username, email, phone_number, first_name, last_name, role 
FROM users 
WHERE username = 'admin';
