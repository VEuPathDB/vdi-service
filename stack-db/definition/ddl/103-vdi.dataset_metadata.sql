/**
 * Mutable Dataset Metadata
 *
 * Tracks additional user-provided metadata about a dataset that may be updated
 * by the owning user at any time.
 *
 * The metadata here is a copy of what is stored in the dataset's metadata file
 * in the source object store.  It is cached in this table for quick lookup by
 * the HTTP REST API.
 */
CREATE TABLE IF NOT EXISTS vdi.dataset_metadata (
  dataset_id VARCHAR
    PRIMARY KEY
    REFERENCES vdi.datasets (dataset_id)
    ON DELETE CASCADE

, visibility VARCHAR
    NOT NULL

, name VARCHAR
    NOT NULL

, summary VARCHAR
    NOT NULL

, project_name VARCHAR

, program_name VARCHAR

, description VARCHAR
);

CREATE INDEX vdi.dataset_metadata_project_name ON vdi.dataset_metadata.