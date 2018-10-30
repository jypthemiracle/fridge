CREATE TABLE member (
    id BIGINT GENERATED BY DEFAULT AS IDENTITY(START WITH 1, INCREMENT BY 1) PRIMARY KEY,
    username VARCHAR(256) NOT NULL,
    password VARCHAR(128) NOT NULL,
    account_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    account_non_locked BOOLEAN NOT NULL DEFAULT TRUE,
    credentials_non_expired BOOLEAN NOT NULL DEFAULT TRUE,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    password_token VARCHAR(20),
    password_token_expire_time TIMESTAMP,
    password_token_try INTEGER DEFAULT 0
);

ALTER TABLE member ADD CONSTRAINT UK_member_username UNIQUE (username);

CREATE TABLE member_authorities (
    member_id BIGINT NOT NULL,
    authority VARCHAR(32) NOT NULL
);