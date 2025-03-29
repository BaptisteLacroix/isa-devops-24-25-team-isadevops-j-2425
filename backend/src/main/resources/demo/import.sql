-- --
-- -- PostgreSQL database dump
-- --
--
-- -- Dumped from database version 17.2 (Debian 17.2-1.pgdg120+1)
-- -- Dumped by pg_dump version 17.2 (Debian 17.2-1.pgdg120+1)
--
-- SET statement_timeout = 0;
-- SET lock_timeout = 0;
-- SET idle_in_transaction_session_timeout = 0;
-- SET transaction_timeout = 0;
-- SET client_encoding = 'UTF8';
-- SET standard_conforming_strings = on;
-- SELECT pg_catalog.set_config('search_path', '', false);
-- SET check_function_bodies = false;
-- SET xmloption = content;
-- SET client_min_messages = warning;
-- SET row_security = off;
--
-- SET default_tablespace = '';
--
-- SET default_table_access_method = heap;
--
-- --
-- -- Name: abstract_perk; Type: TABLE; Schema: public; Owner: postgresuser
-- --
--
-- CREATE TABLE public.abstract_perk (
--                                       partner_id bigint,
--                                       perk_id bigint NOT NULL
-- );
--
--
-- ALTER TABLE public.abstract_perk OWNER TO postgresuser;
--
-- --
-- -- Name: cart; Type: TABLE; Schema: public; Owner: postgresuser
-- --
--
-- CREATE TABLE public.cart (
--                              total_percentage_reduction double precision NOT NULL,
--                              cart_id bigint NOT NULL,
--                              partner_partner_id bigint
-- );
--
--
-- ALTER TABLE public.cart OWNER TO postgresuser;
--
-- --
-- -- Name: cart_item; Type: TABLE; Schema: public; Owner: postgresuser
-- --
--
-- CREATE TABLE public.cart_item (
--                                   consumed boolean,
--                                   price double precision NOT NULL,
--                                   quantity integer,
--                                   cart_id bigint,
--                                   cart_item_id bigint NOT NULL,
--                                   item_item_id bigint,
--                                   start_time timestamp(6) without time zone
-- );
--
--
-- ALTER TABLE public.cart_item OWNER TO postgresuser;
--
-- --
-- -- Name: customer; Type: TABLE; Schema: public; Owner: postgresuser
-- --
--
-- CREATE TABLE public.customer (
--                                  vfp boolean NOT NULL,
--                                  cart_id bigint,
--                                  address character varying(255) NOT NULL,
--                                  card_number character varying(255) NOT NULL,
--                                  email character varying(255) NOT NULL,
--                                  first_name character varying(255) NOT NULL,
--                                  surname character varying(255) NOT NULL
-- );
--
--
-- ALTER TABLE public.customer OWNER TO postgresuser;
--
-- --
-- -- Name: customer_purchase_list; Type: TABLE; Schema: public; Owner: postgresuser
-- --
--
-- CREATE TABLE public.customer_purchase_list (
--                                                purchase_list_purchase_id bigint NOT NULL,
--                                                customer_email character varying(255) NOT NULL
-- );
--
--
-- ALTER TABLE public.customer_purchase_list OWNER TO postgresuser;
--
-- --
-- -- Name: item; Type: TABLE; Schema: public; Owner: postgresuser
-- --
--
-- CREATE TABLE public.item (
--                              price double precision NOT NULL,
--                              item_id bigint NOT NULL,
--                              partner_id bigint,
--                              label character varying(255) NOT NULL
-- );
--
--
-- ALTER TABLE public.item OWNER TO postgresuser;
--
-- --
-- -- Name: npurchasedmgifted_perk; Type: TABLE; Schema: public; Owner: postgresuser
-- --
--
-- CREATE TABLE public.npurchasedmgifted_perk (
--                                                nb_gifted integer NOT NULL,
--                                                nb_purchased integer NOT NULL,
--                                                item_item_id bigint,
--                                                perk_id bigint NOT NULL
-- );
--
--
-- ALTER TABLE public.npurchasedmgifted_perk OWNER TO postgresuser;
--
-- --
-- -- Name: partner; Type: TABLE; Schema: public; Owner: postgresuser
-- --
--
-- CREATE TABLE public.partner (
--                                 partner_id bigint NOT NULL,
--                                 address character varying(255) NOT NULL,
--                                 name character varying(255) NOT NULL
-- );
--
--
-- ALTER TABLE public.partner OWNER TO postgresuser;
--
-- --
-- -- Name: payment; Type: TABLE; Schema: public; Owner: postgresuser
-- --
--
-- CREATE TABLE public.payment (
--                                 amount double precision NOT NULL,
--                                 payment_id bigint NOT NULL,
--                                 "timestamp" timestamp(6) without time zone NOT NULL
-- );
--
--
-- ALTER TABLE public.payment OWNER TO postgresuser;
--
-- --
-- -- Name: purchase; Type: TABLE; Schema: public; Owner: postgresuser
-- --
--
-- CREATE TABLE public.purchase (
--                                  already_consumed_inaperk boolean NOT NULL,
--                                  cart_id bigint,
--                                  partner_id bigint,
--                                  payment_payment_id bigint,
--                                  purchase_id bigint NOT NULL
-- );
--
--
-- ALTER TABLE public.purchase OWNER TO postgresuser;
--
-- --
-- -- Name: timed_discount_in_percent_perk; Type: TABLE; Schema: public; Owner: postgresuser
-- --
--
-- CREATE TABLE public.timed_discount_in_percent_perk (
--                                                        discount_rate double precision NOT NULL,
--                                                        "time" time(6) without time zone NOT NULL,
--                                                        perk_id bigint NOT NULL
-- );
--
--
-- ALTER TABLE public.timed_discount_in_percent_perk OWNER TO postgresuser;
--
-- --
-- -- Name: vfp_discount_in_percent_perk; Type: TABLE; Schema: public; Owner: postgresuser
-- --
--
-- CREATE TABLE public.vfp_discount_in_percent_perk (
--                                                      discount_rate double precision NOT NULL,
--                                                      end_hour time(6) without time zone NOT NULL,
--                                                      start_hour time(6) without time zone NOT NULL,
--                                                      perk_id bigint NOT NULL
-- );
--
--
-- ALTER TABLE public.vfp_discount_in_percent_perk OWNER TO postgresuser;
--


--
-- Data for Name: partner; Type: TABLE DATA; Schema: public; Owner: postgresuser
--
INSERT INTO public.partner (partner_id, address, name) VALUES (1, '14 rue du paindemie, Draguignan', 'Boulange');
INSERT INTO public.partner (partner_id, address, name) VALUES (2, '13 rue des roses, Lorgues', 'Fleuriste');
INSERT INTO public.partner (partner_id, address, name) VALUES (3, '12 rue des viandes, Le Luc', 'Boucherie');
INSERT INTO public.partner (partner_id, address, name) VALUES (4, '11 rue des poissons, Saint-Tropez', 'Poissonnerie');
INSERT INTO public.partner (partner_id, address, name) VALUES (5, '10 rue des fromages, Sainte-Maxime', 'Fromagerie');
INSERT INTO public.partner (partner_id, address, name) VALUES (6, '1 rue des enfants, Nice', 'HappyKids');

--
-- Data for Name: cart; Type: TABLE DATA; Schema: public; Owner: postgresuser
--

INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id) VALUES (1, 0, 1);
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id) VALUES (2, 0, 2);
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id) VALUES (3, 0, 3);
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id) VALUES (4, 0, 4);
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id) VALUES (5, 0, 5);
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id) VALUES (6, 0, 1);
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id) VALUES (7, 0, 2);
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id) VALUES (8, 0, 3);
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id) VALUES (9, 0, 4);
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id) VALUES (10, 0, 5);
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id) VALUES (11, 0, 6);
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id) VALUES (12, 0, 3);
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id) VALUES (13, 0, 1);
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id) VALUES (14, 0, 1);
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id) VALUES (15, 0, 2);
INSERT INTO public.cart (cart_id, total_percentage_reduction, partner_partner_id) VALUES (16, 0, 6);

--
-- Data for Name: customer; Type: TABLE DATA; Schema: public; Owner: postgresuser
--
INSERT INTO public.customer (vfp, cart_id, address, card_number, email, first_name, surname) VALUES (false, 16, 'blabliblou', '1234567890', 'alice.bob@gmail.com', 'Alice', 'bob');
INSERT INTO public.customer (vfp, cart_id, address, card_number, email, first_name, surname) VALUES (false, 10, '2 avenue des militaires, Callas', '1234567891', 'clement@armeedeterre.fr', 'Clement', 'lfv');
INSERT INTO public.customer (vfp, cart_id, address, card_number, email, first_name, surname) VALUES (false, 12, '3 rue des arcsitecte, Draguignan', '1234567892', 'antoine@fitnesspark.fr', 'Antoine', 'fadda');
INSERT INTO public.customer (vfp, cart_id, address, card_number, email, first_name, surname) VALUES (false, 6, '4 rue des pectoraux, Nice', '1234567893', 'antoine@seancepull.fr', 'Antoine', 'maistre');
INSERT INTO public.customer (vfp, cart_id, address, card_number, email, first_name, surname) VALUES (false, 9, '5 rue des anonymes, St Laurent du Var', '1234567894', 'baptiste@tabarnak.fr', 'Baptiste', 'xxx');
INSERT INTO public.customer (vfp, cart_id, address, card_number, email, first_name, surname) VALUES (false, 7, 'Place du capitole, Toulouse', '1234567895', 'roxane@princesse.fr', 'Roxane', 'Roxx');

--
-- Data for Name: item; Type: TABLE DATA; Schema: public; Owner: postgresuser
--
INSERT INTO public.item (price, item_id, partner_id, label) VALUES (10, 21, 6, 'Heure de garde HappyKids');
INSERT INTO public.item (price, item_id, partner_id, label) VALUES (1, 1, 1, 'croissant');
INSERT INTO public.item (price, item_id, partner_id, label) VALUES (1.2, 2, 1, 'baguette');
INSERT INTO public.item (price, item_id, partner_id, label) VALUES (1.5, 3, 1, 'chocolatine');
INSERT INTO public.item (price, item_id, partner_id, label) VALUES (1.8, 4, 1, 'pain au raisin');
INSERT INTO public.item (price, item_id, partner_id, label) VALUES (1, 5, 2, 'rose');
INSERT INTO public.item (price, item_id, partner_id, label) VALUES (1.2, 6, 2, 'tulipe');
INSERT INTO public.item (price, item_id, partner_id, label) VALUES (1.5, 7, 2, 'muguet');
INSERT INTO public.item (price, item_id, partner_id, label) VALUES (1.8, 8, 2, 'orchidee');
INSERT INTO public.item (price, item_id, partner_id, label) VALUES (1, 9, 3, 'steak');
INSERT INTO public.item (price, item_id, partner_id, label) VALUES (1.2, 10, 3, 'saucisse');
INSERT INTO public.item (price, item_id, partner_id, label) VALUES (1.5, 11, 3, 'jambon');
INSERT INTO public.item (price, item_id, partner_id, label) VALUES (1.8, 12, 3, 'poulet');
INSERT INTO public.item (price, item_id, partner_id, label) VALUES (1, 13, 4, 'saumon');
INSERT INTO public.item (price, item_id, partner_id, label) VALUES (1.2, 14, 4, 'cabillaud');
INSERT INTO public.item (price, item_id, partner_id, label) VALUES (1.5, 15, 4, 'sardine');
INSERT INTO public.item (price, item_id, partner_id, label) VALUES (1.8, 16, 4, 'thon');
INSERT INTO public.item (price, item_id, partner_id, label) VALUES (1, 17, 5, 'camembert');
INSERT INTO public.item (price, item_id, partner_id, label) VALUES (1.2, 18, 5, 'roquefort');
INSERT INTO public.item (price, item_id, partner_id, label) VALUES (1.5, 19, 5, 'brie');
INSERT INTO public.item (price, item_id, partner_id, label) VALUES (1.8, 20, 5, 'comte');

-- --
-- -- Data for Name: abstract_perk; Type: TABLE DATA; Schema: public; Owner: postgresuser
-- --
INSERT INTO public.abstract_perk (partner_id, perk_id) VALUES (6, 1);
INSERT INTO public.abstract_perk (partner_id, perk_id) VALUES (2, 2);
INSERT INTO public.abstract_perk (partner_id, perk_id) VALUES (3, 3);
-- Data for Name: timed_discount_in_percent_perk; Type: TABLE DATA; Schema: public; Owner: postgresuser
INSERT INTO public.timed_discount_in_percent_perk (discount_rate, "time", perk_id) VALUES (20, '8:00:00', 2);
-- Data for Name: vfp_discount_in_percent_perk; Type: TABLE DATA; Schema: public; Owner: postgresuser
INSERT INTO public.vfp_discount_in_percent_perk (discount_rate, end_hour, start_hour, perk_id) VALUES (5, '12:00:00', '08:00:00', 1);
-- Data for Name: npurchasedmgifted_perk; Type: TABLE DATA; Schema: public; Owner: postgresuser
INSERT INTO public.npurchasedmgifted_perk (nb_gifted, nb_purchased, item_item_id, perk_id) VALUES (1, 5, 10, 3);
--
-- Data for Name: cart_item; Type: TABLE DATA; Schema: public; Owner: postgresuser
--

INSERT INTO public.cart_item (cart_item_id, consumed, price, quantity, cart_id, item_item_id, start_time) VALUES (1, false, 0, 3, 6, 3, NULL);
INSERT INTO public.cart_item (cart_item_id, consumed, price, quantity, cart_id, item_item_id, start_time) VALUES (2, false, 0, 3, 7, 7, NULL);
INSERT INTO public.cart_item (cart_item_id, consumed, price, quantity, cart_id, item_item_id, start_time) VALUES (3, false, 0, 3, 8, 11, NULL);
INSERT INTO public.cart_item (cart_item_id, consumed, price, quantity, cart_id, item_item_id, start_time) VALUES (4, false, 0, 3, 9, 15, NULL);
INSERT INTO public.cart_item (cart_item_id, consumed, price, quantity, cart_id, item_item_id, start_time) VALUES (5, false, 0, 3, 10, 19, NULL);
INSERT INTO public.cart_item (cart_item_id, consumed, price, quantity, cart_id, item_item_id, start_time) VALUES (6, false, 0, 1, 11, 21, '2025-06-01 10:00:00');
INSERT INTO public.cart_item (cart_item_id, consumed, price, quantity, cart_id, item_item_id, start_time) VALUES (7, false, 0, 3, 12, 11, NULL);
INSERT INTO public.cart_item (cart_item_id, consumed, price, quantity, cart_id, item_item_id, start_time) VALUES (8, false, 3, 3, 13, 1, NULL);
INSERT INTO public.cart_item (cart_item_id, consumed, price, quantity, cart_id, item_item_id, start_time) VALUES (9, false, 1, 1, 14, 1, NULL);
INSERT INTO public.cart_item (cart_item_id, consumed, price, quantity, cart_id, item_item_id, start_time) VALUES (10, false, 15, 1, 14, 3, NULL);
INSERT INTO public.cart_item (cart_item_id, consumed, price, quantity, cart_id, item_item_id, start_time) VALUES (11, false, 2, 2, 15, 5, NULL);
INSERT INTO public.cart_item (cart_item_id, consumed, price, quantity, cart_id, item_item_id, start_time) VALUES (12, false, 0, 1, 16, 21, '2025-06-01 10:00:00');

--
-- Data for Name: payment; Type: TABLE DATA; Schema: public; Owner: postgresuser
--
INSERT INTO public.payment (amount, payment_id, "timestamp") VALUES (3, 1, '2025-03-19 14:00:00');
INSERT INTO public.payment (amount, payment_id, "timestamp") VALUES (2.5, 2, '2025-03-18 10:00:00');
INSERT INTO public.payment (amount, payment_id, "timestamp") VALUES (2, 3, '2025-03-18 10:00:00');

--
-- Data for Name: purchase; Type: TABLE DATA; Schema: public; Owner: postgresuser
--
INSERT INTO public.purchase (already_consumed_inaperk, cart_id, partner_id, payment_payment_id, purchase_id) VALUES (false, 13, 1, 1, 1);
INSERT INTO public.purchase (already_consumed_inaperk, cart_id, partner_id, payment_payment_id, purchase_id) VALUES (false, 14, 1, 2, 2);
INSERT INTO public.purchase (already_consumed_inaperk, cart_id, partner_id, payment_payment_id, purchase_id) VALUES (false, 15, 2, 3, 3);

--
-- Data for Name: customer_purchase_list; Type: TABLE DATA; Schema: public; Owner: postgresuser
--
INSERT INTO public.customer_purchase_list (purchase_list_purchase_id, customer_email) VALUES (2, 'alice.bob@gmail.com');
INSERT INTO public.customer_purchase_list (purchase_list_purchase_id, customer_email) VALUES (1, 'alice.bob@gmail.com');
INSERT INTO public.customer_purchase_list (purchase_list_purchase_id, customer_email) VALUES (3, 'alice.bob@gmail.com');

-- Update Sequences
select setval('abstract_perk_seq', (select max(perk_id) from abstract_perk));
select setval('cart_item_seq', (select max(cart_item_id) from cart_item));
select setval('cart_seq', (select max(cart_id) from cart));
select setval('item_seq', (select max(item_id) from item));
select setval('partner_seq', (select max(partner_id) from partner));
select setval('payment_seq', (select max(payment_id) from payment));
select setval('purchase_seq', (select max(purchase_id) from purchase));

--
-- --
-- -- Name: abstract_perk abstract_perk_pkey; Type: CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.abstract_perk
--     ADD CONSTRAINT abstract_perk_pkey PRIMARY KEY (perk_id);
--
--
-- --
-- -- Name: cart_item cart_item_pkey; Type: CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.cart_item
--     ADD CONSTRAINT cart_item_pkey PRIMARY KEY (cart_item_id);
--
--
-- --
-- -- Name: cart cart_pkey; Type: CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.cart
--     ADD CONSTRAINT cart_pkey PRIMARY KEY (cart_id);
--
--
-- --
-- -- Name: customer customer_cart_id_key; Type: CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.customer
--     ADD CONSTRAINT customer_cart_id_key UNIQUE (cart_id);
--
--
-- --
-- -- Name: customer customer_pkey; Type: CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.customer
--     ADD CONSTRAINT customer_pkey PRIMARY KEY (email);
--
--
-- --
-- -- Name: customer_purchase_list customer_purchase_list_purchase_list_purchase_id_key; Type: CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.customer_purchase_list
--     ADD CONSTRAINT customer_purchase_list_purchase_list_purchase_id_key UNIQUE (purchase_list_purchase_id);
--
--
-- --
-- -- Name: item item_pkey; Type: CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.item
--     ADD CONSTRAINT item_pkey PRIMARY KEY (item_id);
--
--
-- --
-- -- Name: npurchasedmgifted_perk npurchasedmgifted_perk_item_item_id_key; Type: CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.npurchasedmgifted_perk
--     ADD CONSTRAINT npurchasedmgifted_perk_item_item_id_key UNIQUE (item_item_id);
--
--
-- --
-- -- Name: npurchasedmgifted_perk npurchasedmgifted_perk_pkey; Type: CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.npurchasedmgifted_perk
--     ADD CONSTRAINT npurchasedmgifted_perk_pkey PRIMARY KEY (perk_id);
--
--
-- --
-- -- Name: partner partner_pkey; Type: CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.partner
--     ADD CONSTRAINT partner_pkey PRIMARY KEY (partner_id);
--
--
-- --
-- -- Name: payment payment_pkey; Type: CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.payment
--     ADD CONSTRAINT payment_pkey PRIMARY KEY (payment_id);
--
--
-- --
-- -- Name: purchase purchase_cart_id_key; Type: CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.purchase
--     ADD CONSTRAINT purchase_cart_id_key UNIQUE (cart_id);
--
--
-- --
-- -- Name: purchase purchase_payment_payment_id_key; Type: CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.purchase
--     ADD CONSTRAINT purchase_payment_payment_id_key UNIQUE (payment_payment_id);
--
--
-- --
-- -- Name: purchase purchase_pkey; Type: CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.purchase
--     ADD CONSTRAINT purchase_pkey PRIMARY KEY (purchase_id);
--
--
-- --
-- -- Name: timed_discount_in_percent_perk timed_discount_in_percent_perk_pkey; Type: CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.timed_discount_in_percent_perk
--     ADD CONSTRAINT timed_discount_in_percent_perk_pkey PRIMARY KEY (perk_id);
--
--
-- --
-- -- Name: vfp_discount_in_percent_perk vfp_discount_in_percent_perk_pkey; Type: CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.vfp_discount_in_percent_perk
--     ADD CONSTRAINT vfp_discount_in_percent_perk_pkey PRIMARY KEY (perk_id);
--
--
-- --
-- -- Name: cart_item fk1uobyhgl1wvgt1jpccia8xxs3; Type: FK CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.cart_item
--     ADD CONSTRAINT fk1uobyhgl1wvgt1jpccia8xxs3 FOREIGN KEY (cart_id) REFERENCES public.cart(cart_id);
--
--
-- --
-- -- Name: purchase fk4y4piwhfpfja5of6woxhjr77q; Type: FK CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.purchase
--     ADD CONSTRAINT fk4y4piwhfpfja5of6woxhjr77q FOREIGN KEY (cart_id) REFERENCES public.cart(cart_id);
--
--
-- --
-- -- Name: abstract_perk fk7o485nvmji4ye0e01e6r136o; Type: FK CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.abstract_perk
--     ADD CONSTRAINT fk7o485nvmji4ye0e01e6r136o FOREIGN KEY (partner_id) REFERENCES public.partner(partner_id);
--
--
-- --
-- -- Name: customer_purchase_list fk9y90eocm2f6ix6x0h5lvf1v3g; Type: FK CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.customer_purchase_list
--     ADD CONSTRAINT fk9y90eocm2f6ix6x0h5lvf1v3g FOREIGN KEY (customer_email) REFERENCES public.customer(email);
--
--
-- --
-- -- Name: customer_purchase_list fkaaswmm5xm5d9c564kud4uk7sl; Type: FK CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.customer_purchase_list
--     ADD CONSTRAINT fkaaswmm5xm5d9c564kud4uk7sl FOREIGN KEY (purchase_list_purchase_id) REFERENCES public.purchase(purchase_id);
--
--
-- --
-- -- Name: customer fkam4cgy6fxmjm52m8otoph84m3; Type: FK CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.customer
--     ADD CONSTRAINT fkam4cgy6fxmjm52m8otoph84m3 FOREIGN KEY (cart_id) REFERENCES public.cart(cart_id);
--
--
-- --
-- -- Name: purchase fkb8hc39ql3b41v37me53ljinub; Type: FK CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.purchase
--     ADD CONSTRAINT fkb8hc39ql3b41v37me53ljinub FOREIGN KEY (partner_id) REFERENCES public.partner(partner_id);
--
--
-- --
-- -- Name: timed_discount_in_percent_perk fkdpvvb6c6nwxphs3iydmwqu2q7; Type: FK CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.timed_discount_in_percent_perk
--     ADD CONSTRAINT fkdpvvb6c6nwxphs3iydmwqu2q7 FOREIGN KEY (perk_id) REFERENCES public.abstract_perk(perk_id);
--
--
-- --
-- -- Name: npurchasedmgifted_perk fkenml2lnb5cde7mh7ggoup9gnt; Type: FK CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.npurchasedmgifted_perk
--     ADD CONSTRAINT fkenml2lnb5cde7mh7ggoup9gnt FOREIGN KEY (perk_id) REFERENCES public.abstract_perk(perk_id);
--
--
-- --
-- -- Name: vfp_discount_in_percent_perk fkerb2sx5m6lr60v5dxw7b4tatd; Type: FK CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.vfp_discount_in_percent_perk
--     ADD CONSTRAINT fkerb2sx5m6lr60v5dxw7b4tatd FOREIGN KEY (perk_id) REFERENCES public.abstract_perk(perk_id);
--
--
-- --
-- -- Name: npurchasedmgifted_perk fkflcwtdvnxmgmsbji7oryoh9jk; Type: FK CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.npurchasedmgifted_perk
--     ADD CONSTRAINT fkflcwtdvnxmgmsbji7oryoh9jk FOREIGN KEY (item_item_id) REFERENCES public.item(item_id);
--
--
-- --
-- -- Name: cart_item fkg488ogpd18rrfwvqwoef6ui23; Type: FK CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.cart_item
--     ADD CONSTRAINT fkg488ogpd18rrfwvqwoef6ui23 FOREIGN KEY (item_item_id) REFERENCES public.item(item_id);
--
--
-- --
-- -- Name: purchase fkgqvjc5gluu963dquvxesddd7m; Type: FK CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.purchase
--     ADD CONSTRAINT fkgqvjc5gluu963dquvxesddd7m FOREIGN KEY (payment_payment_id) REFERENCES public.payment(payment_id);
--
--
-- --
-- -- Name: item fkkc4dcg68an5krybv88urk1gjf; Type: FK CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.item
--     ADD CONSTRAINT fkkc4dcg68an5krybv88urk1gjf FOREIGN KEY (partner_id) REFERENCES public.partner(partner_id);
--
--
-- --
-- -- Name: cart fkqa0d4f7dyn44l03rylqhabiy0; Type: FK CONSTRAINT; Schema: public; Owner: postgresuser
-- --
--
-- ALTER TABLE ONLY public.cart
--     ADD CONSTRAINT fkqa0d4f7dyn44l03rylqhabiy0 FOREIGN KEY (partner_partner_id) REFERENCES public.partner(partner_id);
--
--
-- --
-- -- PostgreSQL database dump complete
-- --

