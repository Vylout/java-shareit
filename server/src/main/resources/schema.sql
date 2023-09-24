CREATE TABLE IF NOT EXISTS users
(
    user_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name    VARCHAR(255) NOT NULL,
    email   VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS requests (
    request_id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    description  VARCHAR(255) NOT NULL,
    requester_id BIGINT NOT NULL,
    created      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_requests_requester_id FOREIGN KEY (requester_id) REFERENCES users
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS items
(
    item_id      BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name         VARCHAR(255) NOT NULL,
    description  VARCHAR(255) NOT NULL,
    is_available BOOLEAN NOT NULL,
    owner_id     BIGINT NOT NULL,
    request_id   BIGINT,
    CONSTRAINT fk_item_owner_id FOREIGN KEY (owner_id) REFERENCES users
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_item_request_id FOREIGN KEY (request_id) REFERENCES requests
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS bookings
(
    booking_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date TIMESTAMP WITH TIME ZONE NOT NULL,
    item_id BIGINT NOT NULL,
    booker_id BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_bookings_item_id FOREIGN KEY (item_id) REFERENCES items
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_bookings_booker_id FOREIGN KEY (booker_id) REFERENCES users
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS comments
(
    comment_id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    text VARCHAR(300) NOT NULL,
    item_id BIGINT NOT NULL,
    author_id BIGINT NOT NULL,
    created TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT fk_comments_item_id FOREIGN KEY (item_id) REFERENCES items
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_comments_author_id FOREIGN KEY (author_id) REFERENCES users
        ON DELETE CASCADE ON UPDATE CASCADE
)