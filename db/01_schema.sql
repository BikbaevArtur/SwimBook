CREATE TABLE IF NOT EXISTS clients
(
    id    SERIAL PRIMARY KEY,
    name  VARCHAR(50) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(50) NOT NULL UNIQUE
);

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS time_table
(
    order_id uuid PRIMARY KEY DEFAULT uuid_generate_v4(),
    client_id INT NOT NULL,
    date DATE NOT NULL,
    time TIME NOT NULL,
    FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS work_schedule(
    weekday SMALLINT PRIMARY KEY CHECK ( weekday BETWEEN 0 AND 6) ,
    start_time TIME NOT NULL ,
    end_time TIME NOT NULL
);

CREATE TABLE IF NOT EXISTS holiday_schedule(
    date DATE PRIMARY KEY ,
    start_time TIME NOT NULL ,
    end_time TIME NOT NULL

)