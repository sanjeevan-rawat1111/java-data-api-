-- Test Data Setup Script for Java Data API
-- This script sets up test data for both MySQL and Aerospike testing

-- MySQL Test Data Setup
USE mydatabase;

-- Clear existing test data
DELETE FROM users WHERE name LIKE 'Test%' OR name LIKE 'Updated%';

-- Insert test users for API testing
INSERT INTO users (name, email, age, city) VALUES
('Test User 1', 'testuser1@example.com', 25, 'Test City 1'),
('Test User 2', 'testuser2@example.com', 30, 'Test City 2'),
('Test User 3', 'testuser3@example.com', 35, 'Test City 3'),
('Updated Test User', 'updated@example.com', 28, 'Updated City'),
('API Test User', 'apitest@example.com', 22, 'API City');

-- Verify test data
SELECT COUNT(*) as test_user_count FROM users WHERE name LIKE 'Test%' OR name LIKE 'Updated%' OR name LIKE 'API%';

-- Show all test users
SELECT * FROM users WHERE name LIKE 'Test%' OR name LIKE 'Updated%' OR name LIKE 'API%';
