CREATE DATABASE IF NOT EXISTS queueless_campus;

USE queueless_campus;

CREATE TABLE IF NOT EXISTS food_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    available BOOLEAN DEFAULT TRUE
);

CREATE TABLE IF NOT EXISTS orders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    token_number INT NOT NULL UNIQUE,
    student_name VARCHAR(100) NOT NULL,
    pickup_time VARCHAR(30) NOT NULL,
    total_amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(30) DEFAULT 'Pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS order_items (
    id INT AUTO_INCREMENT PRIMARY KEY,
    order_id INT NOT NULL,
    food_id INT,
    food_name VARCHAR(100) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    quantity INT DEFAULT 1,
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (food_id) REFERENCES food_items(id)
);

INSERT INTO food_items (name, price)
SELECT 'Sandwich', 40.00
WHERE NOT EXISTS (
    SELECT 1 FROM food_items WHERE name = 'Sandwich'
);

INSERT INTO food_items (name, price)
SELECT 'Burger', 60.00
WHERE NOT EXISTS (
    SELECT 1 FROM food_items WHERE name = 'Burger'
);

INSERT INTO food_items (name, price)
SELECT 'Pizza', 80.00
WHERE NOT EXISTS (
    SELECT 1 FROM food_items WHERE name = 'Pizza'
);

INSERT INTO food_items (name, price)
SELECT 'Tea', 15.00
WHERE NOT EXISTS (
    SELECT 1 FROM food_items WHERE name = 'Tea'
);

INSERT INTO food_items (name, price)
SELECT 'Coffee', 25.00
WHERE NOT EXISTS (
    SELECT 1 FROM food_items WHERE name = 'Coffee'
);

SELECT * FROM food_items;