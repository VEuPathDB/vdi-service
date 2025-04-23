package vdi.service.server.outputs

import vdi.service.generated.model.DatasetPostResponseBody
import vdi.service.generated.model.DatasetPostResponseBodyImpl
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
