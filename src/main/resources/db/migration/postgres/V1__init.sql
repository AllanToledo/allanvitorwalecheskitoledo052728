CREATE TABLE app_user (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name        VARCHAR(500) NOT NULL,
    email       VARCHAR(500) NOT NULL UNIQUE,
    password    VARCHAR(500) NOT NULL,
    is_admin    BOOLEAN NOT NULL DEFAULT FALSE
);

CREATE TABLE artist (
    id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name    VARCHAR(500) NOT NULL
);

CREATE TABLE album (
    id      UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name    VARCHAR(500) NOT NULL
);

CREATE TABLE author (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_artist   UUID NOT NULL REFERENCES artist(id),
    id_album    UUID NOT NULL REFERENCES album(id)
);
