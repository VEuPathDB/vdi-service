ALTER TABLE vdi.dataset_share_receipts
  ALTER COLUMN dataset_id TYPE VARCHAR
, DROP CONSTRAINT dataset_share_receipts_dataset_id_fkey
, ADD CONSTRAINT dataset_id_fkey
    FOREIGN KEY (dataset_id)
    REFERENCES vdi.datasets (dataset_id)
    ON DELETE CASCADE
;
