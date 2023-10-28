CREATE TABLE IF NOT EXISTS couriers (
                          uuid UUID PRIMARY KEY,
                          name VARCHAR(255),
                          phone_number VARCHAR(20),
                          vehicle_type VARCHAR(20), -- Electric or Normal
                          vehicle_emission DOUBLE PRECISION, -- 0 for electric, emission value for gas
                          last_long DOUBLE PRECISION,
                          last_lat DOUBLE PRECISION,
                          max_capacity INTEGER,
                          status VARCHAR(20) -- FREE, PICKUP, DELIVERY, DEACTIVATED
);

CREATE TABLE IF NOT EXISTS venues (
                        uuid UUID PRIMARY KEY,
                        name VARCHAR(255),
                        type_of_venue VARCHAR(20), -- Restaurant, Marketplace, Food Bank, Shelters
                        long DOUBLE PRECISION,
                        lat DOUBLE PRECISION,
                        is_donating BOOLEAN
);

CREATE TABLE IF NOT EXISTS orders (
                                      uuid UUID PRIMARY KEY,
                                      assigned_courier_id UUID,
                                      pickup_venue_id UUID REFERENCES venues(uuid),
                                      delivery_venue_id UUID REFERENCES venues(uuid),
                                      rating INTEGER,
                                      pickup_time TIMESTAMP,
                                      delivery_time TIMESTAMP,
                                      pickup_distance DOUBLE PRECISION,
                                      delivery_distance DOUBLE PRECISION,
                                      status VARCHAR(20),
                                      capacity INTEGER,
                                      created_at TIMESTAMP
);

CREATE INDEX idx_orders_assigned_courier_id
    ON orders(assigned_courier_id);


CREATE TABLE IF NOT EXISTS courier_order_sort (
                                    id SERIAL PRIMARY KEY,
                                    courier_id UUID REFERENCES couriers(uuid),
                                    order_id UUID REFERENCES orders(uuid),
                                    action_type VARCHAR(10), -- PICKUP or DROPOFF
                                    sort SERIAL,
                                    status VARCHAR, -- IN_PROGRESS / FINISHED
                                    venue_id UUID REFERENCES venues(uuid)
);

INSERT INTO couriers (uuid, name, phone_number, vehicle_type, vehicle_emission, last_long, last_lat, max_capacity, status)
VALUES (gen_random_uuid(), 'Courier 1', '123-456-7890', 'ELECTRIC', 0.2, -73.9876, 40.7488, 10, 'FREE'),
       (gen_random_uuid(), 'Courier 2', '987-654-3210', 'NORMAL', 0.5, -73.9888, 40.7499, 20, 'FREE');

INSERT INTO venues (uuid, name, type_of_venue, long, lat, is_donating)
VALUES (gen_random_uuid(), 'Restaurant A', 'RESTAURANT', -73.9876, 40.7488, false),
       (gen_random_uuid(), 'Marketplace B', 'MARKETPLACE', -73.9888, 40.7499, true),
       (gen_random_uuid(), 'Restaurant C', 'RESTAURANT', -73.9865, 40.7477, false),
       (gen_random_uuid(), 'Shelter D', 'SHELTER', -73.9855, 40.7466, true),
       (gen_random_uuid(), 'Restaurant E', 'RESTAURANT', -73.9860, 40.7470, false),
       (gen_random_uuid(), 'Marketplace F', 'MARKETPLACE', -73.9850, 40.7460, true);

INSERT INTO orders (uuid, assigned_courier_id, pickup_venue_id, delivery_venue_id, rating, pickup_time, delivery_time, pickup_distance, delivery_distance, status, capacity, created_at)
VALUES (gen_random_uuid(), null, (SELECT uuid from venues WHERE name = 'Restaurant C'), (SELECT uuid from venues WHERE name ='Shelter D'), 5, '2023-10-30 08:00:00', '2023-10-30 09:30:00', 2.5, 3.8, 'PICKING_UP', 5, '2023-10-30 07:45:00'),
       (gen_random_uuid(), null, (SELECT uuid from venues WHERE name = 'Restaurant E'), (SELECT uuid from venues WHERE name = 'Marketplace F'), 4, '2023-10-30 10:00:00', '2023-10-30 11:30:00', 1.5, 2.8, 'PICKING_UP', 7, '2023-10-30 09:30:00');

INSERT INTO courier_order_sort (courier_id, order_id, action_type, sort, status, venue_id)
VALUES ((SELECT uuid from couriers WHERE name = 'Courier 1'), (SELECT uuid from orders WHERE rating = 5), 'PICKUP', 1, 'IN_PROGRESS', (SELECT uuid from venues WHERE name = 'Restaurant A')),
       ((SELECT uuid from couriers WHERE name = 'Courier 2'), (SELECT uuid from orders WHERE rating = 4), 'PICKUP', 1, 'IN_PROGRESS', (SELECT uuid from venues WHERE name = 'Marketplace B'));