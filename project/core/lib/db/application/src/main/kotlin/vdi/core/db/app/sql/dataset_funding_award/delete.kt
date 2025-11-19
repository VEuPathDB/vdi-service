package vdi.core.db.app.sql.dataset_funding_award

import io.foxcapades.kdbc.usingPreparedUpdate
import java.sql.Connection
import vdi.core.db.app.sql.Table
import vdi.core.db.app.sql.set
import vdi.model.meta.DatasetID

// language=postgresql
private fun SQL(schema: String) = "DELETE FROM ${schema}.${Table.FundingAwards} WHERE dataset_id = ?"

internal fun Connection.deleteFundingAwards(schema: String, datasetID: DatasetID) =
  usingPreparedUpdate(SQL(schema)) { it[1] = datasetID }