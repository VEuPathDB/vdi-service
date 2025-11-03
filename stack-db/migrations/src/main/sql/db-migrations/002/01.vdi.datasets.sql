-- Remove length limit on dataset_id column.
ALTER TABLE vdi.datasets ALTER COLUMN dataset_id TYPE VARCHAR;
