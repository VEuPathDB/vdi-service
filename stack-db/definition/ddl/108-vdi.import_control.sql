CREATE TABLE IF NOT EXISTS vdi.import_control (
  dataset_id VARCHAR
    PRIMARY KEY
    REFERENCES vdi.datasets (dataset_id)
    ON DELETE CASCADE
, status VARCHAR
    NOT NULL
);
