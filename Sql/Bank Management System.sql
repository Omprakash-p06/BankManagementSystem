-- SQL Script for Project Bank Management System

-- 1. Create the Database
--    Drops the database if it already exists to ensure a clean slate.
--    Then creates the new 'bank_management' database.
DROP DATABASE IF EXISTS bank_management;
CREATE DATABASE bank_management;
USE bank_management;

-- 2. Create the 'employee' table
--    Stores information about bank employees.
CREATE TABLE employee (
    PFNO VARCHAR(20) PRIMARY KEY,
    Fname VARCHAR(50) NOT NULL,
    Lname VARCHAR(50) NOT NULL,
    Designation VARCHAR(50) NOT NULL,
    JoiningDate DATE NOT NULL,
    Address VARCHAR(255),
    ContactNo VARCHAR(15),
    PasswordHash VARCHAR(255) NOT NULL -- Stores BCrypt hashed password
);

-- 3. Create the 'customer' table
--    Stores information about bank customers.
CREATE TABLE customer (
    CIF VARCHAR(20) PRIMARY KEY, -- Customer Information File (Unique ID for customer)
    Fname VARCHAR(50) NOT NULL,
    Lname VARCHAR(50) NOT NULL,
    Identification_No VARCHAR(50) UNIQUE NOT NULL, -- User ID for login
    Contact_No VARCHAR(15),
    Gender VARCHAR(10),
    Birthdate DATE,
    Address VARCHAR(255),
    Home_Branch VARCHAR(100),
    PasswordHash VARCHAR(255) NOT NULL -- Stores BCrypt hashed password
);

-- 4. Create the 'account' table
--    Stores bank account details for customers.
CREATE TABLE account (
    AccNo VARCHAR(20) PRIMARY KEY, -- Account Number (Unique ID for account)
    CIF VARCHAR(20) NOT NULL,      -- Foreign key to customer's CIF
    AccType VARCHAR(20) NOT NULL,  -- e.g., SAVINGS, CURRENT
    Balance DECIMAL(15, 2) DEFAULT 0.00, -- Account balance
    OpeningDate DATE NOT NULL,
    FOREIGN KEY (CIF) REFERENCES customer(CIF) ON DELETE CASCADE ON UPDATE CASCADE
);

-- 5. Create the 'transactions' table
--    Records all deposit and withdrawal transactions.
CREATE TABLE transactions (
    transactionid VARCHAR(50) PRIMARY KEY, -- Unique ID for transaction
    accno VARCHAR(20) NOT NULL,            -- Account involved in transaction
    amount DECIMAL(15, 2) NOT NULL,
    transactiontype ENUM('DEPOSIT', 'WITHDRAWAL') NOT NULL,
    transactiondate TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- makerid can be a CIF (customer) or PFNO (employee)
    -- As discussed, we REMOVED the foreign key constraint here to allow both customer and employee IDs.
    makerid VARCHAR(50) NOT NULL,
    checkerid VARCHAR(50),                 -- Employee PFNO who approved/rejected, NULL if pending
    FOREIGN KEY (accno) REFERENCES account(AccNo) ON DELETE RESTRICT ON UPDATE CASCADE
    -- IMPORTANT: fk_transactions_maker constraint was dropped to allow both customer and employee IDs for makerid
);

-- 6. Create the 'transfers' table
--    Records all fund transfers between accounts.
CREATE TABLE transfers (
    transfer_id VARCHAR(50) PRIMARY KEY,   -- Unique ID for transfer (UUID based, truncated)
    from_accno VARCHAR(20) NOT NULL,       -- Source account
    to_accno VARCHAR(20) NOT NULL,         -- Destination account
    amount DECIMAL(15, 2) NOT NULL,
    transfer_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (from_accno) REFERENCES account(AccNo) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (to_accno) REFERENCES account(AccNo) ON DELETE RESTRICT ON UPDATE CASCADE
);


-- 7. Insert Initial Employee Data
--    Use BCrypt to generate password hashes for these employees.
--    You would typically use a Java utility to generate these hashes.
--    For demonstration, here are some example hashes for common passwords like "password123".
--    **REPLACE THESE WITH ACTUAL HASHES FROM YOUR BCrypt GENERATOR IF YOU WANT TO LOG IN!**
--    Example hash for "password123" is $2a$10$wT0q/P2mE1jQjQc5F2z3r.sM8t9u0v1w2x3y4z5a6b7c8d9e0f1 (this is just an example hash)
--    The values below are placeholders. You MUST generate real bcrypt hashes for your desired passwords.

-- You can use a tool like https://bcrypt-generator.com/ to generate hashes.
-- Or better yet, generate them via your Java application's BCrypt utility if you have one.

INSERT INTO employee (PFNO, Fname, Lname, Designation, JoiningDate, Address, ContactNo, PasswordHash) VALUES
('EMPIN001', 'John', 'Doe', 'Branch Manager', '2020-01-15', '123 Main St, City', '9876543210', '$2a$10$abcdefghijklmnopqrstuvwxyza1234567890abcdefghijklmnop'), -- Placeholder hash for "password123"
('EMPIN002', 'Rahul', 'Singh', 'Operations Manager', '2021-03-20', '456 Oak Ave, Town', '8765432109', '$2a$10$abcdefghijklmnopqrstuvwxyza1234567890abcdefghijklmnop'), -- Placeholder hash for "password123"
('EMPIN003', 'Anjali', 'Gupta', 'Bank Teller', '2022-07-01', '789 Pine Rd, Village', '7654321098', '$2a$10$abcdefghijklmnopqrstuvwxyza1234567890abcdefghijklmnop'); -- Placeholder hash for "password123"
-- Ensure these hashes are generated using BCrypt and replace the placeholder values.

-- 8. Insert Initial Customer Data (Optional, for quick testing)
--    Similar to employees, generate a BCrypt hash for the customer's password.
INSERT INTO customer (CIF, Fname, Lname, Identification_No, Contact_No, Gender, Birthdate, Address, Home_Branch, PasswordHash) VALUES
('CUST001', 'Alice', 'Smith', 'user123', '9988776655', 'Female', '1990-05-10', '101 Elm St, City', 'Main Branch', '$2a$10$abcdefghijklmnopqrstuvwxyza1234567890abcdefghijklmnop'); -- Placeholder hash for "password123"
-- Ensure this hash is generated using BCrypt and replace the placeholder value.

-- 9. Insert Initial Account Data for the Test Customer (Optional, for quick testing)
INSERT INTO account (AccNo, CIF, AccType, Balance, OpeningDate) VALUES
('ACC001', 'CUST001', 'SAVINGS', 1000.00, '2023-01-01');
