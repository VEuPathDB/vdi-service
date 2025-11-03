CREATE TABLE IF NOT EXISTS vdi.dataset_publications (
  dataset_id VARCHAR
    PRIMARY KEY
    REFERENCES vdi.datasets (dataset_id)
    ON DELETE CASCADE
, publication_type SMALLINT
    NOT NULL
, publication_id VARCHAR
    NOT NULL
);

CREATE UNIQUE INDEX IF NOT EXISTS
  vdi_dataset_publications_dataset_to_publication
ON
  vdi.dataset_publications (dataset_id, publication_id);
