CREATE TABLE notifications (
    id VARCHAR(255) PRIMARY KEY,
    order_id VARCHAR(255) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    event_type VARCHAR(255) NOT NULL,
    message VARCHAR(255),
    created_at TIMESTAMP
);
