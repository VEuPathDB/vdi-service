CREATE TABLE IF NOT EXISTS vdi.upload_files (
  dataset_id VARCHAR
    NOT NULL
    REFERENCES vdi.datasets (dataset_id)
    ON DELETE CASCADE
, file_name VARCHAR
    NOT NULL
, file_size BIGINT
    NOT NULL
, PRIMARY KEY (dataset_id, file_name)
);
