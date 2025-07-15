-- First, let's see what users exist
SELECT id, username, email, phone_number, first_name, last_name, role 
FROM users 
ORDER BY id;

-- Update admin user (we know this exists)
UPDATE users 
SET phone_number = '+919876543210', 
    email = 'admin@dreamcollections.com'
WHERE username = 'admin';

-- Update any other existing users
UPDATE users 
SET phone_number = '+91987654321' || id::text, 
    email = CASE 
        WHEN email IS NULL OR email = '' THEN username || '@example.com'
        ELSE email
    END
WHERE phone_number IS NULL;

-- Show final results
SELECT id, username, email, phone_number, first_name, last_name, role 
FROM users 
ORDER BY id;
