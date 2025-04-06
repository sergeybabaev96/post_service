CREATE TABLE feed (
    user_id BIGINT primary key,
    post_ids JSONB NOT NULL
)