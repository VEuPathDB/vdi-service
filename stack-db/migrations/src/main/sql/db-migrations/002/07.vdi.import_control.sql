ALTER TABLE vdi.import_control
  ALTER COLUMN dataset_id TYPE VARCHAR
, DROP CONSTRAINT import_control_dataset_id_fkey
, ADD CONSTRAINT dataset_id_fkey
    FOREIGN KEY (dataset_id)
    REFERENCES vdi.datasets (dataset_id)
    ON DELETE CASCADE
;