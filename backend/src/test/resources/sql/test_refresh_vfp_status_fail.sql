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

