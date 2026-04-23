-- 1. UTILISATEURS
INSERT INTO users (id, username, password, role) VALUES 
(1, 'admin_wsd', 'admin123', 'ADMIN'),
(2, 'jean.chauffeur', 'pass123', 'DRIVER'),
(3, 'sarah.livraison', 'pass123', 'DRIVER'),
(4, 'client.carrefour', 'pass123', 'CLIENT'),
(5, 'client.fnac', 'pass123', 'CLIENT');

-- 2. ACTEURS : CHAUFFEURS & CLIENTS
INSERT INTO driver (id, user_id, first_name, last_name, license_number, status) VALUES 
(1, 2, 'Jean', 'Dupont', 'B-123456789', 'AVAILABLE'),
(2, 3, 'Sarah', 'Connor', 'B-987654321', 'AVAILABLE');

INSERT INTO client (id, user_id, name, siret_number, city) VALUES 
(1, 4, 'Carrefour City Paris', '12345678900012', 'Paris'),
(2, 5, 'Fnac Champs-Elysées', '98765432100098', 'Paris');

-- 3. FLOTTE : MODÈLES & CAMIONS
INSERT INTO vehicle_model (id, brand, model_name, capacity, fuel_consumption, fuel_type, tank_capacity) VALUES 
(1, 'Renault', 'Master L3H2', 13.0, 8.5, 'DIESEL', 80.0),
(2, 'Peugeot', 'e-Expert', 6.1, 0.0, 'ELECTRIC', 100.0);

INSERT INTO truck (id, model_id, license_plate, current_fuel_level, status) VALUES 
(1, 1, 'AB-123-CD', 75.0, 'AVAILABLE'),
(2, 1, 'EF-456-GH', 40.0, 'AVAILABLE'),
(3, 2, 'IJ-789-KL', 100.0, 'AVAILABLE');

-- 4. OPÉRATIONS : COMMANDES EN ATTENTE
INSERT INTO orders (id, client_id, trip_id, address_text, latitude, longitude, requested_date, time_slot, price, quantity, status) VALUES 
(1, 1, NULL, '15 Rue de Rivoli, 75004 Paris', 48.8556, 2.3556, '2026-04-24', '08:00-10:00', 120.50, 5, 'PENDING'),
(2, 1, NULL, '120 Avenue des Champs-Élysées, 75008 Paris', 48.8719, 2.3011, '2026-04-24', '10:00-12:00', 85.00, 2, 'PENDING'),
(3, 2, NULL, '74 Boulevard Saint-Germain, 75005 Paris', 48.8505, 2.3486, '2026-04-24', '14:00-16:00', 210.00, 8, 'PENDING');

-- 5. SYNCHRONISATION DES SÉQUENCES
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('driver_id_seq', (SELECT MAX(id) FROM driver));
SELECT setval('client_id_seq', (SELECT MAX(id) FROM client));
SELECT setval('vehicle_model_id_seq', (SELECT MAX(id) FROM vehicle_model));
SELECT setval('truck_id_seq', (SELECT MAX(id) FROM truck));
SELECT setval('orders_id_seq', (SELECT MAX(id) FROM orders));