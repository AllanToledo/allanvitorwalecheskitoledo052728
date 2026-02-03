CREATE TABLE regional(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_extern   BIGINT NOT NULL,
    name        VARCHAR(200) DEFAULT '',
    active      BOOLEAN DEFAULT FALSE
);