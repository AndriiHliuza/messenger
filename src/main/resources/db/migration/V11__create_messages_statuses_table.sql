DROP TABLE IF EXISTS messages_statuses;
CREATE TABLE messages_statuses (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    message_id UUID NOT NULL,
    user_id UUID NOT NULL,
    user_type VARCHAR(128) NOT NULL,
    status VARCHAR(128) NOT NULL,
    FOREIGN KEY (message_id) REFERENCES messages (id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT messages_statuses_user_type_check CHECK(user_type IN ('SENDER', 'RECEIVER')),
    CONSTRAINT messages_statuses_status_check CHECK(status IN ('READ_MESSAGE', 'UNREAD_MESSAGE')),
    CONSTRAINT messages_statuses_unique_constraint UNIQUE (message_id, user_id)
);