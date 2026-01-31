CREATE TABLE file(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    bucket      VARCHAR(500),
    name        VARCHAR(500),
    media_type  VARCHAR(500),
    size        BIGINT,
    UNIQUE (bucket, name)
);

CREATE TABLE cover(
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    id_file     UUID NOT NULL REFERENCES file(id),
    id_album    UUID NOT NULL REFERENCES album(id)
);