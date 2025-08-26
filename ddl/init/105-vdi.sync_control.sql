CREATE TABLE IF NOT EXISTS vdi.sync_control (
  dataset_id VARCHAR
    PRIMARY KEY
    REFERENCES vdi.datasets (dataset_id)
    ON DELETE CASCADE
, shares_update_time TIMESTAMP WITH TIME ZONE
    NOT NULL
, data_update_time TIMESTAMP WITH TIME ZONE
    NOT NULL
, meta_update_time TIMESTAMP WITH TIME ZONE
    NOT NULL
);
