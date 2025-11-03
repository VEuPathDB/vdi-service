ALTER TABLE vdi.dataset_projects
  ALTER COLUMN dataset_id TYPE VARCHAR
, DROP CONSTRAINT dataset_projects_dataset_id_fkey
, ADD CONSTRAINT dataset_id_fkey
    FOREIGN KEY (dataset_id)
    REFERENCES vdi.datasets (dataset_id)
    ON DELETE CASCADE
;
