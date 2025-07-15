-- Update the admin user that exists with proper email and phone
UPDATE users 
SET email = 'admin@dreamcollections.com',
    phone_number = '+919876543210'
WHERE role = 'ADMIN' OR username LIKE '%admin%';

-- Show all users to verify
SELECT id, username, email, phone_number, first_name, last_name, role 
FROM users 
ORDER BY id;
