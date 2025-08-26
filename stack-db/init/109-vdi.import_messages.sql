CREATE TABLE IF NOT EXISTS vdi.import_messages (
  dataset_id VARCHAR
    NOT NULL
    REFERENCES vdi.datasets (dataset_id)
    ON DELETE CASCADE
, message VARCHAR
    NOT NULL
);

CREATE INDEX IF NOT EXISTS vdi.import_messages_dataset_id ON vdi.import_messages (dataset_id);
