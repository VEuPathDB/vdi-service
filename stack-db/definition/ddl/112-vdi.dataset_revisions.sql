/**
 * Dataset revision tracking table.
 *
 * This table provides a way for VDI to link dataset revisions together by the
 * original dataset ID.
 *
 * From any given revision ID VDI can get the original ID, then use that ID to
 * fetch the most recent revision based on timestamp.
 *
 * The created date in the vdi.datasets table will be copied from revision to
 * revision as the date the dataset was originally created, while the timestamp
 * here will track when the revision was made.
 */
CREATE TABLE IF NOT EXISTS vdi.dataset_revisions (
  revision_id VARCHAR     PRIMARY KEY -- new dataset ID
, original_id VARCHAR     NOT NULL    -- ORIGINAL dataset ID (not the immediate parent)
, action      SMALLINT    NOT NULL    -- Action enum ID
, timestamp   TIMESTAMPTZ NOT NULL    -- timestamp the revision was created
);

CREATE INDEX IF NOT EXISTS vdi_dataset_revisions_original_id ON vdi.dataset_revisions (original_id);
