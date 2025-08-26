CREATE TABLE IF NOT EXISTS vdi.dataset_share_receipts (
  dataset_id VARCHAR
    NOT NULL
    REFERENCES vdi.datasets (dataset_id)
, recipient_id VARCHAR
    NOT NULL
, status VARCHAR
    NOT NULL
, PRIMARY KEY (dataset_id, recipient_id)
);
