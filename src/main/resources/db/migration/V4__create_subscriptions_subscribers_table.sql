DROP TABLE IF EXISTS subscriptions_subscribers;
CREATE TABLE subscriptions_subscribers (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    subscription_id UUID NOT NULL,
    subscriber_id UUID NOT NULL,
    FOREIGN KEY (subscription_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (subscriber_id) REFERENCES users (id) ON DELETE CASCADE
);