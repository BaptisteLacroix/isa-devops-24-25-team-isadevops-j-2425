--
-- Data for Name: partner; Type: TABLE DATA; Schema: public; Owner: postgresuser
--
INSERT INTO public.partner (partner_id, address, name)
VALUES (1, '14 rue du paindemie, Draguignan', 'Boulange');
INSERT INTO public.partner (partner_id, address, name)
VALUES (2, '13 rue des roses, Lorgues', 'Fleuriste');
INSERT INTO public.partner (partner_id, address, name)
VALUES (3, '12 rue des viandes, Le Luc', 'Boucherie');
INSERT INTO public.partner (partner_id, address, name)
VALUES (4, '11 rue des poissons, Saint-Tropez', 'Poissonnerie');
INSERT INTO public.partner (partner_id, address, name)
VALUES (5, '10 rue des fromages, Sainte-Maxime', 'Fromagerie');
INSERT INTO public.partner (partner_id, address, name)
VALUES (6, '1 rue des enfants, Nice', 'HappyKids');

--
-- Data for Name: cart; Type: TABLE DATA; Schema: public; Owner: postgresuser
--
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id)
VALUES (1, 0, 1);
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id)
VALUES (2, 0, 2);
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id)
VALUES (3, 0, 3);
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id)
VALUES (4, 0, 4);
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id)
VALUES (5, 0, 5);
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id)
VALUES (6, 0, 1);
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id)
VALUES (7, 0, 2);
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id)
VALUES (8, 0, 3);
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id)
VALUES (9, 0, 4);
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id)
VALUES (10, 0, 5);
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id)
VALUES (11, 0, 6);
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id)
VALUES (12, 0, 3);
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id)
VALUES (13, 0, 1);
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id)
VALUES (14, 0, 1);
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id)
VALUES (15, 0, 2);
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id)
VALUES (16, 0, 6);

--
-- Data for Name: customer; Type: TABLE DATA; Schema: public; Owner: postgresuser
--
INSERT INTO public.customer (vfp, cart_id, address, card_number, email, first_name, surname)
VALUES (false, 16, 'blabliblou', '1234567890', 'alice.bob@gmail.com', 'Alice', 'bob');
INSERT INTO public.customer (vfp, cart_id, address, card_number, email, first_name, surname)
VALUES (false, 10, '2 avenue des militaires, Callas', '1234567891', 'clement@armeedeterre.fr', 'Clement', 'lfv');
INSERT INTO public.customer (vfp, cart_id, address, card_number, email, first_name, surname)
VALUES (false, 12, '3 rue des arcsitecte, Draguignan', '1234567892', 'antoine@fitnesspark.fr', 'Antoine', 'fadda');
INSERT INTO public.customer (vfp, cart_id, address, card_number, email, first_name, surname)
VALUES (false, 6, '4 rue des pectoraux, Nice', '1234567893', 'antoine@seancepull.fr', 'Antoine', 'maistre');
INSERT INTO public.customer (vfp, cart_id, address, card_number, email, first_name, surname)
VALUES (false, 9, '5 rue des anonymes, St Laurent du Var', '1234567894', 'baptiste@tabarnak.fr', 'Baptiste', 'xxx');
INSERT INTO public.customer (vfp, cart_id, address, card_number, email, first_name, surname)
VALUES (false, 7, 'Place du capitole, Toulouse', '1234567895', 'roxane@princesse.fr', 'Roxane', 'Roxx');
INSERT INTO public.customer (vfp, cart_id, address, card_number, email, first_name, surname)
VALUES (true, 11, '6 rue des orthopésistes parce que Pierre nous fait mal aux Pieds avec ses cailloux, Gallet', '1234567896', 'pierre.cailloux@cafaismal.auxpieds', 'Pierre', 'Cailloux');

--
-- Data for Name: item; Type: TABLE DATA; Schema: public; Owner: postgresuser
--
INSERT INTO public.item (price, item_id, partner_id, label)
VALUES (10, 21, 6, 'Heure de garde HappyKids');
INSERT INTO public.item (price, item_id, partner_id, label)
VALUES (1, 1, 1, 'croissant');
INSERT INTO public.item (price, item_id, partner_id, label)
VALUES (1.2, 2, 1, 'baguette');
INSERT INTO public.item (price, item_id, partner_id, label)
VALUES (1.5, 3, 1, 'chocolatine');
INSERT INTO public.item (price, item_id, partner_id, label)
VALUES (1.8, 4, 1, 'pain au raisin');
INSERT INTO public.item (price, item_id, partner_id, label)
VALUES (1, 5, 2, 'rose');
INSERT INTO public.item (price, item_id, partner_id, label)
VALUES (1.2, 6, 2, 'tulipe');
INSERT INTO public.item (price, item_id, partner_id, label)
VALUES (1.5, 7, 2, 'muguet');
INSERT INTO public.item (price, item_id, partner_id, label)
VALUES (1.8, 8, 2, 'orchidee');
INSERT INTO public.item (price, item_id, partner_id, label)
VALUES (1, 9, 3, 'steak');
INSERT INTO public.item (price, item_id, partner_id, label)
VALUES (1.2, 10, 3, 'saucisse');
INSERT INTO public.item (price, item_id, partner_id, label)
VALUES (1.5, 11, 3, 'jambon');
INSERT INTO public.item (price, item_id, partner_id, label)
VALUES (1.8, 12, 3, 'poulet');
INSERT INTO public.item (price, item_id, partner_id, label)
VALUES (1, 13, 4, 'saumon');
INSERT INTO public.item (price, item_id, partner_id, label)
VALUES (1.2, 14, 4, 'cabillaud');
INSERT INTO public.item (price, item_id, partner_id, label)
VALUES (1.5, 15, 4, 'sardine');
INSERT INTO public.item (price, item_id, partner_id, label)
VALUES (1.8, 16, 4, 'thon');
INSERT INTO public.item (price, item_id, partner_id, label)
VALUES (1, 17, 5, 'camembert');
INSERT INTO public.item (price, item_id, partner_id, label)
VALUES (1.2, 18, 5, 'roquefort');
INSERT INTO public.item (price, item_id, partner_id, label)
VALUES (1.5, 19, 5, 'brie');
INSERT INTO public.item (price, item_id, partner_id, label)
VALUES (1.8, 20, 5, 'comte');

-- --
-- -- Data for Name: abstract_perk; Type: TABLE DATA; Schema: public; Owner: postgresuser
-- --
INSERT INTO public.abstract_perk (partner_id, perk_id)
VALUES (6, 1);
INSERT INTO public.abstract_perk (partner_id, perk_id)
VALUES (2, 2);
-- Data for Name: timed_discount_in_percent_perk; Type: TABLE DATA; Schema: public; Owner: postgresuser
INSERT INTO public.timed_discount_in_percent_perk (discount_rate, "time", perk_id)
VALUES (20, now() - interval '1 hours', 2);
-- Data for Name: vfp_discount_in_percent_perk; Type: TABLE DATA; Schema: public; Owner: postgresuser
INSERT INTO public.vfp_discount_in_percent_perk (discount_rate, end_hour, start_hour, perk_id)
VALUES (5, '12:00:00', '08:00:00', 1);

--
-- Data for Name: cart_item; Type: TABLE DATA; Schema: public; Owner: postgresuser
--

INSERT INTO public.cart_item (cart_item_id, consumed, price, quantity, cart_id, item_item_id, start_time)
VALUES (1, false, 0, 3, 6, 3, NULL);
INSERT INTO public.cart_item (cart_item_id, consumed, price, quantity, cart_id, item_item_id, start_time)
VALUES (2, false, 0, 3, 7, 7, NULL);
INSERT INTO public.cart_item (cart_item_id, consumed, price, quantity, cart_id, item_item_id, start_time)
VALUES (3, false, 0, 3, 8, 11, NULL);
INSERT INTO public.cart_item (cart_item_id, consumed, price, quantity, cart_id, item_item_id, start_time)
VALUES (4, false, 0, 3, 9, 15, NULL);
INSERT INTO public.cart_item (cart_item_id, consumed, price, quantity, cart_id, item_item_id, start_time)
VALUES (5, false, 0, 3, 10, 19, NULL);
INSERT INTO public.cart_item (cart_item_id, consumed, price, quantity, cart_id, item_item_id, start_time)
VALUES (6, false, 0, 1, 11, 21, '2025-06-01 10:00:00');
INSERT INTO public.cart_item (cart_item_id, consumed, price, quantity, cart_id, item_item_id, start_time)
VALUES (7, false, 0, 3, 12, 11, NULL);
INSERT INTO public.cart_item (cart_item_id, consumed, price, quantity, cart_id, item_item_id, start_time)
VALUES (8, false, 3, 3, 13, 1, NULL);
INSERT INTO public.cart_item (cart_item_id, consumed, price, quantity, cart_id, item_item_id, start_time)
VALUES (9, false, 1, 1, 14, 1, NULL);
INSERT INTO public.cart_item (cart_item_id, consumed, price, quantity, cart_id, item_item_id, start_time)
VALUES (10, false, 15, 1, 14, 3, NULL);
INSERT INTO public.cart_item (cart_item_id, consumed, price, quantity, cart_id, item_item_id, start_time)
VALUES (11, false, 2, 2, 15, 5, NULL);
INSERT INTO public.cart_item (cart_item_id, consumed, price, quantity, cart_id, item_item_id, start_time)
VALUES (12, false, 0, 1, 16, 21, '2025-06-01 10:00:00');

--
-- Data for Name: payment; Type: TABLE DATA; Schema: public; Owner: postgresuser
--
INSERT INTO public.payment (amount, payment_id, "timestamp")
VALUES (3, 1, '2025-03-19 14:00:00');
INSERT INTO public.payment (amount, payment_id, "timestamp")
VALUES (2.5, 2, '2025-03-18 10:00:00');
INSERT INTO public.payment (amount, payment_id, "timestamp")
VALUES (2, 3, '2025-03-18 10:00:00');

--
-- Data for Name: purchase; Type: TABLE DATA; Schema: public; Owner: postgresuser
--
INSERT INTO public.purchase (already_consumed_inaperk, cart_id, partner_id, payment_payment_id, purchase_id)
VALUES (false, 13, 1, 1, 1);
INSERT INTO public.purchase (already_consumed_inaperk, cart_id, partner_id, payment_payment_id, purchase_id)
VALUES (false, 14, 1, 2, 2);
INSERT INTO public.purchase (already_consumed_inaperk, cart_id, partner_id, payment_payment_id, purchase_id)
VALUES (false, 15, 2, 3, 3);

--
-- Data for Name: customer_purchase_list; Type: TABLE DATA; Schema: public; Owner: postgresuser
--
INSERT INTO public.customer_purchase_list (purchase_list_purchase_id, customer_email)
VALUES (2, 'alice.bob@gmail.com');
INSERT INTO public.customer_purchase_list (purchase_list_purchase_id, customer_email)
VALUES (1, 'alice.bob@gmail.com');
INSERT INTO public.customer_purchase_list (purchase_list_purchase_id, customer_email)
VALUES (3, 'alice.bob@gmail.com');

-- Update Sequences
select setval('abstract_perk_seq', (select max(perk_id) from abstract_perk));
select setval('cart_item_seq', (select max(cart_item_id) from cart_item));
select setval('cart_seq', (select max(cart_id) from cart));
select setval('item_seq', (select max(item_id) from item));
select setval('partner_seq', (select max(partner_id) from partner));
select setval('payment_seq', (select max(payment_id) from payment));
select setval('purchase_seq', (select max(purchase_id) from purchase));
