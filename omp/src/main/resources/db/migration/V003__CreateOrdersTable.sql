CREATE TABLE orders
(
    id              UUID PRIMARY KEY,
    seat_letter     VARCHAR(5),
    seat_number     INT,
    status          VARCHAR(20),
    buyer_email     VARCHAR(255),
    products_qty    JSONB,
    total_price     DECIMAL(10, 2),
    payment_status  VARCHAR(50),
    payment_date    TIMESTAMP,
    payment_gateway VARCHAR(100)
);