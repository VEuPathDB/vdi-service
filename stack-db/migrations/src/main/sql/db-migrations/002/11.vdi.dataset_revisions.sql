CREATE TABLE IF NOT EXISTS vdi.dataset_revisions (
  revision_id VARCHAR     PRIMARY KEY -- new dataset ID
, original_id VARCHAR     NOT NULL    -- ORIGINAL dataset ID (not the immediate parent)
, action      SMALLINT    NOT NULL    -- Action enum ID
, timestamp   TIMESTAMPTZ NOT NULL    -- timestamp the revision was created
);

CREATE INDEX IF NOT EXISTS vdi.dataset_revisions_original_id ON vdi.dataset_revisions (original_id);
