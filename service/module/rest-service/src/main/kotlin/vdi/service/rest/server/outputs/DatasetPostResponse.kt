package vdi.service.rest.server.outputs

import org.veupathdb.vdi.lib.common.field.DatasetID
import vdi.service.rest.generated.model.DatasetPostResponseBody

/**
 * Creates a new [DatasetPostResponseBody] instance wrapping the given [datasetID].
 *
 * @param datasetID Dataset ID for the response.
 *
 * @return A new [DatasetPostResponseBody] instance.
 */
fun DatasetPostResponseBody(datasetID: DatasetID): DatasetPostResponseBody =
  vdi.service.rest.generated.model.DatasetPostResponseBodyImpl().also { it.datasetId = datasetID.toString() }
