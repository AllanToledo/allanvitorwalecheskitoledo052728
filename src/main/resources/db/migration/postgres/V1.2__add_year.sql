ALTER TABLE album ADD COLUMN year INT;
-- noinspection SqlWithoutWhere
UPDATE album SET year = 9999;

ALTER TABLE album ALTER COLUMN year SET NOT NULL;
