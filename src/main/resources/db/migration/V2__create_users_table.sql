DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(256) UNIQUE NOT NULL,
    password VARCHAR(256) NOT NULL,
    registration_date TIMESTAMPTZ NOT NULL DEFAULT current_timestamp,
    firstname VARCHAR(128) NOT NULL,
    lastname VARCHAR(128) NOT NULL,
    birthday DATE,
    role VARCHAR(128) NOT NULL DEFAULT 'USER',
    CONSTRAINT users_username_check CHECK(username ~*'^[^[:space:]]+@[^[:space:]]+$'),
    CONSTRAINT users_role_check CHECK(role IN ('USER', 'ADMIN', 'ROOT'))
);
