/**
 * Core dataset details.
 *
 * This table contains the critical, immutable information about a dataset.
 */
CREATE TABLE IF NOT EXISTS vdi.datasets (
  dataset_id VARCHAR
    PRIMARY KEY
    NOT NULL

, type_name VARCHAR
    NOT NULL

, type_version VARCHAR
    NOT NULL

  -- External system ID of the user who created/owns the dataset.
, owner_id VARCHAR

  -- Indicates whether the dataset owner has marked the dataset as deleted.
  -- Datasets are soft-deleted and kept in the system for a configured duration
  -- before being purged.
, is_deleted BOOLEAN
    NOT NULL
    DEFAULT FALSE

, origin VARCHAR
    NOT NULL

  -- Tracks the ORIGINAL creation date of the dataset.
  --
  -- If the dataset has multiple revisions, the created date here will be that
  -- of the original dataset creation.
  --
  -- This value may differ from the time the dataset was first uploaded to VDI
  -- if the dataset is being imported from an external system.
, created TIMESTAMP WITH TIME ZONE
    NOT NULL

  -- Tracks the time the record was inserted into the database.
  --
  -- This value is for debugging and process validation purposes only, it will
  -- be different between VDI instances, and cache database rebuilds.
, inserted TIMESTAMP WITH TIME ZONE
    NOT NULL
    DEFAULT now()
);
