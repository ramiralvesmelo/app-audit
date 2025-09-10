-- CUSTOMERS
INSERT INTO customers (name, email, phone) VALUES
  ('Alice', 'alice@example.com', '11999990001'),
  ('Bob',   'bob@example.com',   '11999990002');

-- PRODUCTS
INSERT INTO products (name, description, price, stock, sku, version) VALUES
  ('Teclado Mecânico', 'Switch azul', 350.00, 20, 'SKU-TEC-001', 0),
  ('Mouse Gamer',      '16000 DPI',   199.90, 50, 'SKU-MOU-002', 0);

-- ORDERS (ex.: pedido pendente da Alice)
INSERT INTO orders (order_number, order_date, customer_id, status, total_amount)
VALUES ('ORD-0001', NOW(), (SELECT id FROM customers WHERE email='alice@example.com'), 'PENDENTE', 0.00);

-- ORDER ITEMS (calcula subtotal = unit_price * quantity)
INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal)
SELECT o.id, p.id, 2, p.price, p.price * 2
FROM orders o
JOIN products p ON p.sku='SKU-TEC-001'
WHERE o.order_number='ORD-0001';

-- Atualiza total do pedido
UPDATE orders o
SET total_amount = (SELECT COALESCE(SUM(subtotal),0) FROM order_items oi WHERE oi.order_id = o.id)
WHERE o.order_number='ORD-0001';