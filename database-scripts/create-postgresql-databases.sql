-- PostgreSQL Database Creation Script for DreamCollections Microservices
-- Run this script as postgres superuser to create the databases

-- Create databases for each microservice
CREATE DATABASE dreamcollections_identity;
CREATE DATABASE dreamcollections_catalog;
CREATE DATABASE dreamcollections_cart;
CREATE DATABASE dreamcollections_order;

-- Grant all privileges to postgres user (adjust if using different user)
GRANT ALL PRIVILEGES ON DATABASE dreamcollections_identity TO postgres;
GRANT ALL PRIVILEGES ON DATABASE dreamcollections_catalog TO postgres;
GRANT ALL PRIVILEGES ON DATABASE dreamcollections_cart TO postgres;
GRANT ALL PRIVILEGES ON DATABASE dreamcollections_order TO postgres;

-- List databases to verify creation
\l
