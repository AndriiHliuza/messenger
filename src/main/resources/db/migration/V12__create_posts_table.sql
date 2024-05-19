DROP TABLE IF EXISTS posts CASCADE;
CREATE TABLE posts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID,
    title TEXT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT current_timestamp,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE
);