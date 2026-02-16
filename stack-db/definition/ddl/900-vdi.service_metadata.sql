CREATE TABLE IF NOT EXISTS vdi.service_metadata (
  key   VARCHAR PRIMARY KEY
, value VARCHAR
);

INSERT INTO vdi.service_metadata (key, value) VALUES ('db.version', 3)
