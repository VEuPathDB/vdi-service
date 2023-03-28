package vdi.component.handler.client

import java.io.InputStream
import vdi.components.common.fields.DatasetID

interface HandlerClient {

  fun submitImport(datasetID: DatasetID, payload: () -> InputStream): ImportResponse

  fun submitInstallData(datasetID: DatasetID, payload: () -> InputStream): InstallDataResponse

  fun submitInstallMeta(datasetID: DatasetID, meta:)

}