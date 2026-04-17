-- Database schema for the Java Data API application
-- This script creates the necessary tables for MySQL database

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS mydatabase;
USE mydatabase;

-- Create users table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    age INT,
    city VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create index on email for faster lookups
CREATE INDEX idx_users_email ON users(email);

-- Create index on city for faster lookups
CREATE INDEX idx_users_city ON users(city);

-- Create index on age for faster range queries
CREATE INDEX idx_users_age ON users(age);

-- Insert sample data
INSERT INTO users (name, email, age, city) VALUES
('John Doe', 'john.doe@example.com', 30, 'New York'),
('Jane Smith', 'jane.smith@example.com', 25, 'Los Angeles'),
('Bob Johnson', 'bob.johnson@example.com', 35, 'Chicago'),
('Alice Brown', 'alice.brown@example.com', 28, 'New York'),
('Charlie Wilson', 'charlie.wilson@example.com', 42, 'San Francisco'),
('Diana Davis', 'diana.davis@example.com', 33, 'Los Angeles'),
('Eve Miller', 'eve.miller@example.com', 29, 'Chicago'),
('Frank Garcia', 'frank.garcia@example.com', 31, 'Miami'),
('Grace Lee', 'grace.lee@example.com', 27, 'Seattle'),
('Henry Taylor', 'henry.taylor@example.com', 38, 'Boston');
