-- ============================================
-- DATABASE ADMIN SETUP SCRIPT
-- ============================================
-- Purpose: One-time database and user creation
-- Requires: MySQL root or admin privileges
-- Usage: mysql -u root -p < src/main/resources/db/db-setup-admin.sql
-- ============================================

-- Create database if it doesn't exist
CREATE DATABASE IF NOT EXISTS db_experiment;

-- Create user if it doesn't exist
CREATE USER IF NOT EXISTS 'dbexp'@'localhost' IDENTIFIED BY 'A1b212345';

-- Grant all privileges on the database to the user
GRANT ALL PRIVILEGES ON db_experiment.* TO 'dbexp'@'localhost';

-- Apply privilege changes
FLUSH PRIVILEGES;

-- Verification queries (optional - can be run separately)
-- SELECT User, Host FROM mysql.user WHERE User = 'dbexp';
-- SHOW DATABASES LIKE 'db_experiment';
-- SHOW GRANTS FOR 'dbexp'@'localhost';