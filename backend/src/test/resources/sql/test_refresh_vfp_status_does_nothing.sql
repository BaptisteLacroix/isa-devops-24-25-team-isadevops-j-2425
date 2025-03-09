-- Customer with no enough purchase to be VFP but some in the week
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
VALUES (2, 10, '2025-03-10 10:00:00');
INSERT INTO purchase (purchase_id, payment_payment_id, already_consumed_inaperk)
VALUES (2, 2, false);
INSERT INTO customer_purchase_list (purchase_list_purchase_id, customer_email)
VALUES (2, 'alice@gmail.com');

-- Customer that is VFP and has enough purchase stay VFP next week
INSERT INTO customer(email, first_name, surname, card_number, address, vfp)
VALUES ('bob@gmail.com',
        'Bob',
        'bob',
        'blabliblou',
        '1 rue de la paix',
        true);

INSERT INTO payment(payment_id, amount, timestamp)
VALUES (6, 10, '2025-03-03 10:00:00');
INSERT INTO purchase (purchase_id, payment_payment_id, already_consumed_inaperk)
VALUES (6, 6, false);
INSERT INTO customer_purchase_list (purchase_list_purchase_id, customer_email)
VALUES (6, 'bob@gmail.com');

INSERT INTO payment(payment_id, amount, timestamp)
VALUES (7, 10, '2025-03-04 10:00:00');
INSERT INTO purchase (purchase_id, payment_payment_id, already_consumed_inaperk)
VALUES (7, 7, false);
INSERT INTO customer_purchase_list (purchase_list_purchase_id, customer_email)
VALUES (7, 'bob@gmail.com');

INSERT INTO payment(payment_id, amount, timestamp)
VALUES (8, 10, '2025-03-09 10:00:00');
INSERT INTO purchase (purchase_id, payment_payment_id, already_consumed_inaperk)
VALUES (8, 8, false);
INSERT INTO customer_purchase_list (purchase_list_purchase_id, customer_email)
VALUES (8, 'bob@gmail.com');
