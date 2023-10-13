DROP TABLE IF EXISTS jwts CASCADE;
CREATE TABLE jwts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    content VARCHAR(512) UNIQUE NOT NULL,
    type VARCHAR(128) NOT NULL,
    user_id UUID NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT jwts_type_check CHECK(type IN ('BEARER'))
);