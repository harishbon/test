CREATE TABLE customers (
     id        INTEGER      NOT NULL AUTO_INCREMENT,
     firstname VARCHAR(128) NOT NULL,
     lastname  VARCHAR(128) NOT NULL,
     PRIMARY KEY (id)
);
CREATE TABLE orders (
    id          INTEGER      NOT NULL AUTO_INCREMENT,
    customer_id INTEGER      NOT NULL,
    address     VARCHAR(128) NOT NULL,
    totalPrice  DECIMAL      NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (customer_id) REFERENCES customers(id)
);