DROP TABLE IF EXISTS user_images;
CREATE TABLE user_images (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name TEXT NOT NULL,
    type VARCHAR(128) NOT NULL,
    data BYTEA NOT NULL,
    user_id UUID UNIQUE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);