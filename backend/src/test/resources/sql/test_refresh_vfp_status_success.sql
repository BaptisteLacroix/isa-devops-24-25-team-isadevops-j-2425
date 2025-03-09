-- Customer with just enough purchase to be VFP in the week
INSERT INTO customer(email, first_name, surname, card_number, address, vfp)
VALUES ('alice@gmail.com',
        'Alice',
        'bob',
        'blabliblou',
        '1 rue de la paix',
        false);

INSERT INTO payment(payment_id, amount, timestamp)
VALUES (1, 10, '2025-03-03 10:00:00');
INSERT INTO purchase (purchase_id, payment_payment_id, already_consumed_inaperk)
VALUES (1, 1, false);
INSERT INTO customer_purchase_list (purchase_list_purchase_id, customer_email)
VALUES (1, 'alice@gmail.com');

INSERT INTO payment(payment_id, amount, timestamp)
VALUES (2, 10, '2025-03-04 10:00:00');
INSERT INTO purchase (purchase_id, payment_payment_id, already_consumed_inaperk)
VALUES (2, 2, false);
INSERT INTO customer_purchase_list (purchase_list_purchase_id, customer_email)
VALUES (2, 'alice@gmail.com');

-- Customer with more than enough purchase to be VFP in the week
INSERT INTO customer(email, first_name, surname, card_number, address, vfp)
VALUES ('john@gmail.com',
        'John',
        'john',
        'blabliblou',
        '1 rue de la paix',
        false);

INSERT INTO payment(payment_id, amount, timestamp)
VALUES (3, 10, '2025-03-02 10:00:00');
INSERT INTO purchase (purchase_id, payment_payment_id, already_consumed_inaperk)
VALUES (3, 3, false);
INSERT INTO customer_purchase_list (purchase_list_purchase_id, customer_email)
VALUES (3, 'john@gmail.com');

INSERT INTO payment(payment_id, amount, timestamp)
VALUES (4, 10, '2025-03-04 10:00:00');
INSERT INTO purchase (purchase_id, payment_payment_id, already_consumed_inaperk)
VALUES (4, 4, false);
INSERT INTO customer_purchase_list (purchase_list_purchase_id, customer_email)
VALUES (4, 'john@gmail.com');

INSERT INTO payment(payment_id, amount, timestamp)
VALUES (5, 10, '2025-03-05 10:00:00');
INSERT INTO purchase (purchase_id, payment_payment_id, already_consumed_inaperk)
VALUES (5, 5, false);
INSERT INTO customer_purchase_list (purchase_list_purchase_id, customer_email)
VALUES (5, 'john@gmail.com');
