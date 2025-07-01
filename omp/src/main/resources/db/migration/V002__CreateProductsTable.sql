CREATE TABLE products
(
    id          UUID PRIMARY KEY,
    name        VARCHAR(255)   NOT NULL,
    price       NUMERIC(10, 2) NOT NULL,
    category_id UUID REFERENCES categories (id),
    image       VARCHAR(1024),
    stock       INT            NOT NULL
);