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