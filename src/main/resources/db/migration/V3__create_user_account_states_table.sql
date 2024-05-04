DROP TABLE IF EXISTS user_accounts;
CREATE TABLE user_accounts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID UNIQUE NOT NULL ,
    state VARCHAR(128) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT current_timestamp,
    activated_at TIMESTAMPTZ,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT user_accounts_status_check CHECK(state IN ('ACTIVATED', 'REQUIRE_ACTIVATION', 'BLOCKED')),
    CONSTRAINT user_accounts_unique_constraint UNIQUE (user_id, state)
);