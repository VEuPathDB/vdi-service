ALTER TABLE vdi.import_messages
  ALTER COLUMN dataset_id TYPE VARCHAR
, DROP CONSTRAINT IF EXISTS import_messages_dataset_id_key
, ADD CONSTRAINT dataset_id_fkey
    FOREIGN KEY (dataset_id)
    REFERENCES vdi.datasets (dataset_id)
    ON DELETE CASCADE
;