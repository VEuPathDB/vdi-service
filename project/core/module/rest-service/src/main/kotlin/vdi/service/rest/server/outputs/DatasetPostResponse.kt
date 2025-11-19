package vdi.service.rest.server.outputs

import vdi.model.meta.DatasetID
import vdi.service.rest.generated.model.DatasetPostResponseBody
import vdi.service.rest.generated.model.DatasetPostResponseBodyImpl

/**
 * Creates a new [DatasetPostResponseBody] instance wrapping the given [datasetID].
 *
 * @param datasetID Dataset ID for the response.
 *
 * @return A new [DatasetPostResponseBody] instance.
 */
fun DatasetPostResponseBody(datasetID: DatasetID): DatasetPostResponseBody =
  DatasetPostResponseBodyImpl().also { it.datasetId = datasetID.toString() }
