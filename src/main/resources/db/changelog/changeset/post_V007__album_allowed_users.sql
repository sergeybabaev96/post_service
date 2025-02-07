CREATE TABLE album_allowed_users (
    id bigserial PRIMARY KEY,
    album_id bigint not null,
    user_id bigint not null,

    CONSTRAINT fk_album_user_id FOREIGN KEY (album_id) REFERENCES album (id)
);