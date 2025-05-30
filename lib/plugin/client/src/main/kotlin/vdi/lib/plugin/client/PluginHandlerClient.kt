package vdi.lib.plugin.client

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.common.model.VDIDatasetType
import java.io.InputStream
import vdi.lib.plugin.client.response.imp.ImportResponse
import vdi.lib.plugin.client.response.ind.InstallDataResponse
import vdi.lib.plugin.client.response.inm.InstallMetaResponse
import vdi.lib.plugin.client.response.uni.UninstallResponse

interface PluginHandlerClient {

  /**
   * `POST`s an import request to the configured plugin handler server with the
   * given details and user upload.
   *
   * @param datasetID ID of the dataset to be imported.
   *
   * @param meta Metadata about the dataset to be imported.
   *
   * @param upload `tar.gz` archive containing the user upload file(s).
   *
   * @return An [ImportResponse] instance wrapping the raw response from the
   * plugin handler server.
   */
  suspend fun postImport(datasetID: DatasetID, meta: VDIDatasetMeta, upload: InputStream): ImportResponse

  /**
   * `POST`s an install-meta request to the configured plugin handler server
   * with the given details.
   *
   * @param datasetID ID of the dataset to have its metadata installed.
   *
   * @param projectID Target project for the metadata installation.
   *
   * @param meta Metadata to install.
   *
   * @return An [InstallMetaResponse] instance wrapping the raw response from
   * the plugin handler server.
   */
  suspend fun postInstallMeta(datasetID: DatasetID, projectID: ProjectID, meta: VDIDatasetMeta): InstallMetaResponse

  /**
   * `POST`s an install-data request to the configured plugin handler server
   * with the given details and data payload.
   *
   * @param datasetID ID of the dataset to have its data installed.
   *
   * @param projectID Target project for the data installation.
   *
   * @param meta Dataset Metadata.
   *
   * @param manifest Dataset post-import manifest.
   *
   * @param payload Imported data zip stream.
   *
   * @return An [InstallDataResponse] instance wrapping the raw response from
   * the plugin handler server.
   */
  suspend fun postInstallData(
    datasetID: DatasetID,
    projectID: ProjectID,
    meta: InputStream,
    manifest: InputStream,
    payload: InputStream,
  ): InstallDataResponse

  /**
   * `POST`s an uninstall request to the configured plugin handler server.
   *
   * @param datasetID ID of the dataset to be uninstalled.
   *
   * @param projectID Target project for the uninstallation.
   *
   * @return An [UninstallResponse] instance wrapping the raw response from the
   * plugin handler server.
   */
  suspend fun postUninstall(datasetID: DatasetID, projectID: ProjectID, type: VDIDatasetType): UninstallResponse
}
