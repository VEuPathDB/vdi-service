package vdi.core.db.cache.sql.datasets

import io.foxcapades.kdbc.get
import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import java.sql.Connection
import vdi.core.db.cache.model.RelatedDataset
import vdi.core.db.jdbc.setDatasetID
import vdi.model.meta.DataType
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetPublication
import vdi.model.meta.DatasetType

// language=postgresql
private const val SQL = """
WITH root_publications AS (
  SELECT
    publication_id
  FROM
    vdi.dataset_publications
  WHERE
    dataset_id = ?
)
SELECT
  p.dataset_id       -- 1
, d.type_name
, d.type_version     -- 3
, m.name
, m.summary          -- 5
, d.created
, p.publication_id   -- 7
, p.publication_type
FROM
  vdi.dataset_publications p
  INNER JOIN vdi.datasets d
    USING (dataset_id)
  INNER JOIN vdi.dataset_metadata m
    USING (dataset_id)
WHERE
  dataset_id != ?
  AND publication_id IN (SELECT publication_id FROM root_publications)
"""

internal fun Connection.selectDatasetsByCommonPublication(rootDatasetID: DatasetID) =
  withPreparedStatement(SQL) {
    setDatasetID(1, rootDatasetID)
    setDatasetID(2, rootDatasetID)

    withResults { map {
      RelatedDataset(
        datasetID    = DatasetID(it[1]),
        datasetType  = DatasetType(DataType.of(it[2]), it[3]),
        name         = it[4],
        summary      = it[5],
        created      = it[6],
        relationType = RelatedDataset.RelationType.Publication,
        publication  = RelatedDataset.PublicationRef(it[7], DatasetPublication.PublicationType.entries[it[8]])
      )
    } }
  }