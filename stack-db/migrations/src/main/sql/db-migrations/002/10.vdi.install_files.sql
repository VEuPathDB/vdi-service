ALTER TABLE vdi.install_files
  ALTER COLUMN dataset_id TYPE VARCHAR
, DROP CONSTRAINT install_files_dataset_id_fkey
, ADD CONSTRAINT dataset_id_fkey
    FOREIGN KEY (dataset_id)
    REFERENCES vdi.datasets (dataset_id)
    ON DELETE CASCADE
;
