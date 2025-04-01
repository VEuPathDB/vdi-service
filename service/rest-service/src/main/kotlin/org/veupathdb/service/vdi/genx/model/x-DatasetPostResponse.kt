package org.veupathdb.service.vdi.genx.model

import org.veupathdb.service.vdi.generated.model.DatasetPostResponseBody
import org.veupathdb.service.vdi.generated.model.DatasetPostResponseBodyImpl
import org.veupathdb.vdi.lib.common.field.DatasetID

/**
 * Creates a new [DatasetPostResponseBody] instance wrapping the given [datasetID].
 *
 * @param datasetID Dataset ID for the response.
 *
 * @return A new [DatasetPostResponseBody] instance.
 */
fun DatasetPostResponseBody(datasetID: DatasetID): DatasetPostResponseBody =
  DatasetPostResponseBodyImpl().also { it.datasetId = datasetID.toString() }
