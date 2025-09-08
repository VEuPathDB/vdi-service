package vdi.core.db.app.sql.dataset_funding_award

import io.foxcapades.kdbc.set
import io.foxcapades.kdbc.withPreparedBatchUpdate
import java.sql.Connection
import vdi.core.db.app.sql.set
import vdi.model.data.DatasetFundingAward
import vdi.model.data.DatasetID

// language=postgresql
private fun SQL(schema: String) = "INSERT INTO ${schema}.dataset_funding_award VALUES (?, ?, ?)"

internal fun Connection.insertFundingAwards(
  schema: String,
  datasetID: DatasetID,
  awards: Iterable<DatasetFundingAward>,
) =
  withPreparedBatchUpdate(SQL(schema), awards) {
    set(1, datasetID)
    set(2, it.agency)
    set(3, it.awardNumber)
  }.reduceOrNull(Int::plus) ?: 0