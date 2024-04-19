DROP TABLE IF EXISTS chats_members;
CREATE TABLE chats_members (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    chat_id UUID NOT NULL,
    member_id UUID NOT NULL,
    member_role VARCHAR(128) NOT NULL,
    FOREIGN KEY (chat_id) REFERENCES chats (id) ON DELETE CASCADE,
    FOREIGN KEY (member_id) REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT chats_members_member_role_check CHECK(member_role IN ('ADMIN', 'MEMBER')),
    CONSTRAINT chats_members_unique_constraint UNIQUE (chat_id, member_id)
);