-- DROP na ordem certa
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS customers CASCADE;

-- CREATE
CREATE TABLE customers (
  id           BIGSERIAL PRIMARY KEY,
  name         VARCHAR(255) NOT NULL,
  email        VARCHAR(255) NOT NULL UNIQUE,
  phone        VARCHAR(255)
);

CREATE TABLE products (
  id           BIGSERIAL PRIMARY KEY,
  name         VARCHAR(255) NOT NULL,
  description  VARCHAR(255),
  price        NUMERIC(38,2),
  stock        INTEGER,
  sku          VARCHAR(255) UNIQUE,
  version      BIGINT
);

CREATE TABLE orders (
  id            BIGSERIAL PRIMARY KEY,
  order_number  VARCHAR(255) UNIQUE,
  order_date    TIMESTAMP,
  customer_id   BIGINT REFERENCES customers(id),
  status        VARCHAR(20) NOT NULL,     -- << Enum como STRING
  total_amount  NUMERIC(38,2)
);

CREATE TABLE order_items (
  id          BIGSERIAL PRIMARY KEY,
  order_id    BIGINT REFERENCES orders(id) ON DELETE CASCADE,
  product_id  BIGINT NOT NULL REFERENCES products(id),
  quantity    INTEGER NOT NULL,
  unit_price  NUMERIC(38,2),
  subtotal    NUMERIC(38,2)
);

CREATE INDEX IF NOT EXISTS idx_orders_customer      ON orders(customer_id);
CREATE INDEX IF NOT EXISTS idx_order_items_order    ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_products_sku         ON products(sku);