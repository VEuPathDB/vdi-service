package vdi.core.db.cache.sql.datasets

import io.foxcapades.kdbc.get
import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withPreparedStatement
import io.foxcapades.kdbc.withResults
import java.sql.Connection
import vdi.core.db.cache.model.RelatedDataset
import vdi.core.db.jdbc.setUserID
import vdi.model.data.DataType
import vdi.model.data.DatasetID
import vdi.model.data.DatasetType
import vdi.model.data.UserID

// language=postgresql
private const val SQL = """
SELECT
  d.dataset_id
, d.type_name
, d.type_version
, m.name
, m.summary
, d.created
FROM
  vdi.datasets d
  INNER JOIN vdi.dataset_metadata m
    USING (dataset_id)
WHERE
  d.owner_id = ?
  AND m.project_name = ?
"""

internal fun Connection.selectDatasetsByProjectName(ownerID: UserID, projectName: String) =
  withPreparedStatement(SQL) {
    setUserID(1, ownerID)
    setString(2, projectName)

    withResults { map {
      RelatedDataset(
        datasetID    = DatasetID(it[1]),
        datasetType  = DatasetType(DataType.of(it[2]), it[3]),
        name         = it[4],
        summary      = it[5],
        created      = it[6],
        relationType = RelatedDataset.RelationType.ProjectName,
      )
    } }
  }