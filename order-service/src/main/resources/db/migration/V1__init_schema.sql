CREATE TABLE orders (
    id VARCHAR(255) PRIMARY KEY,
    user_id VARCHAR(255) NOT NULL,
    product_id VARCHAR(255) NOT NULL,
    quantity INT NOT NULL,
    total_price NUMERIC(19, 2) NOT NULL,
    status VARCHAR(255) NOT NULL,
    created_at TIMESTAMP
);
