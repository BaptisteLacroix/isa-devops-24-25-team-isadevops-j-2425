-- Customer with enough purchase to be VFP but enough before the week
INSERT INTO customer(email, first_name, surname, card_number, address, vfp)
VALUES ('bob@gmail.com',
        'Bob',
        'bob',
        'blabliblou',
        '1 rue de la paix',
        false);

INSERT INTO payment(payment_id, amount, timestamp)
VALUES (6, 10, '2025-02-25 10:00:00');
INSERT INTO purchase (purchase_id, payment_payment_id, already_consumed_inaperk)
VALUES (6, 6, false);
INSERT INTO customer_purchase_list (purchase_list_purchase_id, customer_email)
VALUES (6, 'bob@gmail.com');

INSERT INTO payment(payment_id, amount, timestamp)
VALUES (7, 10, '2025-02-28 10:00:00');
INSERT INTO purchase (purchase_id, payment_payment_id, already_consumed_inaperk)
VALUES (7, 7, false);
INSERT INTO customer_purchase_list (purchase_list_purchase_id, customer_email)
VALUES (7, 'bob@gmail.com');

INSERT INTO payment(payment_id, amount, timestamp)
VALUES (8, 10, '2025-03-03 10:00:00');
INSERT INTO purchase (purchase_id, payment_payment_id, already_consumed_inaperk)
VALUES (8, 8, false);
INSERT INTO customer_purchase_list (purchase_list_purchase_id, customer_email)
VALUES (8, 'bob@gmail.com');
