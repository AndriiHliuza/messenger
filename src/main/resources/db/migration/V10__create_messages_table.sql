DROP TABLE IF EXISTS messages CASCADE;
CREATE TABLE messages (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    sender_id UUID,
    chat_id UUID NOT NULL,
    content TEXT NOT NULL,
    send_time TIMESTAMPTZ NOT NULL DEFAULT current_timestamp,
    type VARCHAR(128) NOT NULL,
    FOREIGN KEY (sender_id) REFERENCES users (id) ON DELETE SET NULL,
    FOREIGN KEY (chat_id) REFERENCES chats (id) ON DELETE CASCADE,
    CONSTRAINT messages_type_check CHECK (type IN ('NEW_MESSAGE',
                                                   'MODIFIED_MESSAGE',
                                                   'DELETED_MESSAGE',
                                                   'CHAT_MEMBER_ADDED_TO_CHAT',
                                                   'CHAT_MEMBER_DELETED_FROM_CHAT',
                                                   'CHAT_MEMBER_LEFT_CHAT',
                                                   'MODIFIED_CHAT',
                                                   'NEW_STATUS_IN_CHAT_MEMBER'))
);