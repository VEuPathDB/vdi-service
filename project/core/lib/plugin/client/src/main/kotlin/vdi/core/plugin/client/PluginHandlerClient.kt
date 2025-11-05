package vdi.core.plugin.client

import vdi.model.data.DatasetID
import vdi.model.data.InstallTargetID
import vdi.model.data.DatasetMetadata
import vdi.model.data.DatasetType
import java.io.InputStream
import vdi.core.plugin.client.response.PluginResponse
import vdi.model.EventID

interface PluginHandlerClient {

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
    eventID: EventID,
    datasetID: DatasetID,
    meta: DatasetMetadata,
    upload: InputStream,
  ): PluginResponse

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
    eventID: EventID,
    datasetID: DatasetID,
    installTarget: InstallTargetID,
    meta: DatasetMetadata,
  ): PluginResponse

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
    eventID: EventID,
    datasetID: DatasetID,
    installTarget: InstallTargetID,
    meta: InputStream,
    manifest: InputStream,
    payload: InputStream,
  ): PluginResponse

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
    eventID: EventID,
    datasetID: DatasetID,
    installTarget: InstallTargetID,
    type: DatasetType,
  ): PluginResponse
}
