ALTER TABLE vdi.import_messages
  ALTER COLUMN dataset_id TYPE VARCHAR
, DROP CONSTRAINT IF EXISTS import_messages_dataset_id_key
, ADD CONSTRAINT dataset_id_fkey
    FOREIGN KEY (dataset_id)
    REFERENCES vdi.datasets (dataset_id)
    ON DELETE CASCADE
;

CREATE INDEX IF NOT EXISTS vdi_import_messages_dataset_id ON vdi.import_messages (dataset_id);
CREATE UNIQUE INDEX IF NOT EXISTS vdi_import_messages_message_uq ON vdi.import_messages (dataset_id, message);
