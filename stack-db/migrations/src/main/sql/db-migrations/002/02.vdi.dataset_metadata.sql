ALTER TABLE vdi.dataset_metadata
  ALTER COLUMN dataset_id TYPE VARCHAR
, DROP COLUMN source_url
, ADD COLUMN project_name VARCHAR
, ADD COLUMN program_name VARCHAR
, DROP CONSTRAINT dataset_metadata_dataset_id_fkey
, ADD CONSTRAINT dataset_id_fkey
    FOREIGN KEY (dataset_id)
    REFERENCES vdi.dataset (dataset_id)
    ON DELETE CASCADE
;

CREATE INDEX IF NOT EXISTS vdi_dataset_metadata_project_name ON vdi.dataset_metadata (project_name);
CREATE INDEX IF NOT EXISTS vdi_dataset_metadata_program_name ON vdi.dataset_metadata (program_name);

