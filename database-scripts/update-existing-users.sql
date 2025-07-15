-- Update existing users with phone numbers and proper email addresses

-- Update admin user
UPDATE users 
SET phone_number = '+919876543210', 
    email = 'admin@dreamcollections.com'
WHERE username = 'admin';

-- Update existing customer users with Indian phone numbers and proper emails
UPDATE users 
SET phone_number = '+919876543211', 
    email = 'vijay@example.com'
WHERE username = 'vijay';

UPDATE users 
SET phone_number = '+919876543212', 
    email = 'priya.sharma@gmail.com'
WHERE username = 'priya_sharma';

UPDATE users 
SET phone_number = '+919876543213', 
    email = 'arjun.patel@yahoo.com'
WHERE username = 'arjun_patel';

UPDATE users 
SET phone_number = '+919876543214', 
    email = 'sneha.gupta@hotmail.com'
WHERE username = 'sneha_gupta';

UPDATE users 
SET phone_number = '+919876543215', 
    email = 'rahul.singh@gmail.com'
WHERE username = 'rahul_singh';

UPDATE users 
SET phone_number = '+919876543216', 
    email = 'kavya.reddy@outlook.com'
WHERE username = 'kavya_reddy';

UPDATE users 
SET phone_number = '+919876543217', 
    email = 'amit.kumar@gmail.com'
WHERE username = 'amit_kumar';

UPDATE users 
SET phone_number = '+919876543218', 
    email = 'pooja.jain@yahoo.in'
WHERE username = 'pooja_jain';

UPDATE users 
SET phone_number = '+919876543219', 
    email = 'ravi.sharma@rediffmail.com'
WHERE username = 'ravi_sharma';

UPDATE users 
SET phone_number = '+919876543220', 
    email = 'anita.verma@gmail.com'
WHERE username = 'anita_verma';

UPDATE users 
SET phone_number = '+919876543221', 
    email = 'suresh.nair@hotmail.com'
WHERE username = 'suresh_nair';

-- Display updated users
SELECT id, username, email, phone_number, first_name, last_name, role 
FROM users 
ORDER BY id;
