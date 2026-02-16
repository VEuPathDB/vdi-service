/**
 * Dataset upload status recording.
 */
CREATE TABLE IF NOT EXISTS vdi.upload_status (
  dataset_id VARCHAR
    NOT NULL
    REFERENCES vdi.datasets (dataset_id)
    ON DELETE CASCADE
, status VARCHAR(8) -- running, success, failed
    NOT NULL
    DEFAULT 'running'
, CONSTRAINT upload_status_uq UNIQUE (dataset_id)
);
