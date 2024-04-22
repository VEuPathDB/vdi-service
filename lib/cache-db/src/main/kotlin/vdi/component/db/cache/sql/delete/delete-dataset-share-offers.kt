package vdi.component.db.cache.sql.delete

import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.component.db.cache.util.preparedUpdate
import vdi.component.db.cache.util.setDatasetID
import java.sql.Connection

// language=postgresql
private const val SQL = """
DELETE FROM
  vdi.dataset_share_offers
WHERE
  dataset_id = ?
"""

internal fun Connection.deleteDatasetShareOffers(datasetID: DatasetID) =
  preparedUpdate(SQL) { setDatasetID(1, datasetID) }
