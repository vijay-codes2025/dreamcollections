-- Identity Service Database Sample Data (PostgreSQL)
-- Run this script against the PostgreSQL dreamcollections database

-- Clear existing data (optional - be careful in production)
-- DELETE FROM users;

-- Insert sample users
-- Note: Passwords are hashed using BCrypt for 'password123'
-- You can generate BCrypt hashes using online tools or Spring Security's BCryptPasswordEncoder

INSERT INTO users (username, password_hash, email, first_name, last_name, role) VALUES
-- Admin users
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P2.nRuvO.H5XVe', 'admin@dreamcollections.com', 'Admin', 'User', 'ROLE_ADMIN'),
('manager', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P2.nRuvO.H5XVe', 'manager@dreamcollections.com', 'Store', 'Manager', 'ROLE_ADMIN'),

-- Customer users (Mix of Indian and International names)
('vijay', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P2.nRuvO.H5XVe', 'vijay@example.com', 'Vijay', 'Kumar', 'ROLE_CUSTOMER'),
('priya_sharma', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P2.nRuvO.H5XVe', 'priya.sharma@gmail.com', 'Priya', 'Sharma', 'ROLE_CUSTOMER'),
('rahul_patel', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P2.nRuvO.H5XVe', 'rahul.patel@yahoo.com', 'Rahul', 'Patel', 'ROLE_CUSTOMER'),
('anita_singh', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P2.nRuvO.H5XVe', 'anita.singh@hotmail.com', 'Anita', 'Singh', 'ROLE_CUSTOMER'),
('arjun_reddy', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P2.nRuvO.H5XVe', 'arjun.reddy@gmail.com', 'Arjun', 'Reddy', 'ROLE_CUSTOMER'),
('kavya_nair', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P2.nRuvO.H5XVe', 'kavya.nair@outlook.com', 'Kavya', 'Nair', 'ROLE_CUSTOMER'),
('rohit_gupta', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P2.nRuvO.H5XVe', 'rohit.gupta@gmail.com', 'Rohit', 'Gupta', 'ROLE_CUSTOMER'),
('sneha_iyer', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P2.nRuvO.H5XVe', 'sneha.iyer@rediffmail.com', 'Sneha', 'Iyer', 'ROLE_CUSTOMER'),
('karthik_krishnan', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P2.nRuvO.H5XVe', 'karthik.krishnan@gmail.com', 'Karthik', 'Krishnan', 'ROLE_CUSTOMER'),
('meera_joshi', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9P2.nRuvO.H5XVe', 'meera.joshi@yahoo.in', 'Meera', 'Joshi', 'ROLE_CUSTOMER');

-- Verify the data
SELECT id, username, email, first_name, last_name, role FROM users ORDER BY id;
