package vdi.core.db.cache.sql.datasets

import io.foxcapades.kdbc.map
import io.foxcapades.kdbc.withStatementResults
import java.sql.Connection
import vdi.core.db.cache.model.DeletedDataset
import vdi.core.db.cache.util.getProjectIDList
import vdi.core.db.jdbc.getDataType
import vdi.core.db.jdbc.getUserID
import vdi.core.db.jdbc.reqDatasetID
import vdi.model.data.DatasetType

// language=postgresql
private const val SQL = """
SELECT
  dataset_id
, owner_id
, array(SELECT p.project_id FROM vdi.dataset_projects AS p WHERE p.dataset_id = vd.dataset_id) AS projects
, type_name
, type_version
FROM
  vdi.datasets AS vd
WHERE
  is_deleted = TRUE
"""

internal fun Connection.selectDeletedDatasets(): List<DeletedDataset> =
  createStatement().withStatementResults(SQL) { map {
    DeletedDataset(
      it.reqDatasetID("dataset_id"),
      it.getUserID("owner_id"),
      it.getProjectIDList("projects"),
      DatasetType(it.getDataType("type_name"), it.getString("type_version"))
    )
  } }
