DROP TABLE IF EXISTS user_account_activation_codes;
CREATE TABLE user_account_activation_codes (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    account_id UUID UNIQUE NOT NULL,
    code VARCHAR(256) NOT NULL,
    FOREIGN KEY (account_id) REFERENCES user_accounts (id) ON DELETE CASCADE
);