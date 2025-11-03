ALTER TABLE vdi.dataset_share_offers
  ALTER COLUMN dataset_id TYPE VARCHAR
, DROP CONSTRAINT dataset_share_offers_dataset_id_fkey
, ADD CONSTRAINT dataset_id_fkey
    FOREIGN KEY (dataset_id)
    REFERENCES vdi.datasets (dataset_id)
    ON DELETE CASCADE
;
