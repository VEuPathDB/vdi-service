CREATE TABLE IF NOT EXISTS vdi.import_control (
  dataset_id VARCHAR
    PRIMARY KEY
    REFERENCES vdi.datasets (dataset_id)
, status VARCHAR
    NOT NULL
);
