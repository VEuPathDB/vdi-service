package org.veupathdb.service.vdi.generated.model

import org.veupathdb.vdi.lib.common.field.DatasetID

/**
 * Creates a new [DatasetPostResponse] instance wrapping the given [datasetID].
 *
 * @param datasetID Dataset ID for the response.
 *
 * @return A new [DatasetPostResponse] instance.
 */
fun DatasetPostResponse(datasetID: DatasetID): DatasetPostResponse =
  DatasetPostResponseImpl().also { it.datasetId = datasetID.toString() }