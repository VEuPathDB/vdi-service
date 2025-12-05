package vdi.core.plugin.client

import java.io.InputStream
import vdi.core.plugin.client.model.DataPropertiesFile
import vdi.core.plugin.client.response.ImportResponse
import vdi.core.plugin.client.response.InstallDataResponse
import vdi.core.plugin.client.response.InstallMetaResponse
import vdi.core.plugin.client.response.ServerErrorResponse
import vdi.core.plugin.client.response.UninstallResponse
import vdi.io.plugin.responses.ServerInfoResponse
import vdi.model.EventID
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetMetadata
import vdi.model.meta.DatasetType
import vdi.model.meta.InstallTargetID
import vdi.util.fn.Either

interface PluginHandlerClient {

  suspend fun getServerInfo(): Either<ServerInfoResponse, ServerErrorResponse>

  /**
   * `POST`s an import request to the configured plugin handler server with the
   * given details and user upload.
   *
   * @param eventID Tracing ID for the VDI event being processed.
   *
   * @param datasetID ID of the dataset to be imported.
   *
   * @param meta Metadata about the dataset to be imported.
   *
   * @param upload `tar.gz` archive containing the user upload file(s).
   */
  suspend fun postImport(
    eventID:   EventID,
    datasetID: DatasetID,
    meta:      DatasetMetadata,
    upload:    InputStream,
  ): ImportResponse

  /**
   * `POST`s an install-meta request to the configured plugin handler server
   * with the given details.
   *
   * @param eventID Tracing ID for the VDI event being processed.
   *
   * @param datasetID ID of the dataset to have its metadata installed.
   *
   * @param installTarget Target project for the metadata installation.
   *
   * @param meta Metadata to install.
   */
  suspend fun postInstallMeta(
    eventID:       EventID,
    datasetID:     DatasetID,
    installTarget: InstallTargetID,
    meta:          DatasetMetadata,
    dataPropFiles: Iterable<DataPropertiesFile>,
  ): InstallMetaResponse

  /**
   * `POST`s an install-data request to the configured plugin handler server
   * with the given details and data payload.
   *
   * @param eventID Tracing ID for the VDI event being processed.
   *
   * @param datasetID ID of the dataset to have its data installed.
   *
   * @param installTarget Target project for the data installation.
   *
   * @param meta Dataset Metadata.
   *
   * @param manifest Dataset post-import manifest.
   *
   * @param payload Imported data zip stream.
   */
  suspend fun postInstallData(
    eventID:       EventID,
    datasetID:     DatasetID,
    installTarget: InstallTargetID,
    meta:          InputStream,
    manifest:      InputStream,
    payload:       InputStream,
    dataPropFiles: Iterable<DataPropertiesFile>,
  ): InstallDataResponse

  /**
   * `POST`s an uninstall request to the configured plugin handler server.
   *
   * @param eventID Tracing ID for the VDI event being processed.
   *
   * @param datasetID ID of the dataset to be uninstalled.
   *
   * @param installTarget Target project for the uninstallation.
   */
  suspend fun postUninstall(
    eventID:       EventID,
    datasetID:     DatasetID,
    installTarget: InstallTargetID,
    type:          DatasetType,
  ): UninstallResponse
}
