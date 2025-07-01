CREATE TABLE categories
(
    id                 UUID PRIMARY KEY,
    name               VARCHAR(255) NOT NULL,
    parent_category_id UUID REFERENCES categories (id)
);