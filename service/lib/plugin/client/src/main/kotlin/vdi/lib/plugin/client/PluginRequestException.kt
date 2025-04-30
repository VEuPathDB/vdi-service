package vdi.lib.plugin.client

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID

class PluginRequestException : PluginException {
  private constructor(
    action: String,
    plugin: String,
    projectID: ProjectID,
    userID: UserID,
    datasetID: DatasetID,
    message: String,
  ) : super(action, plugin, projectID, userID, datasetID, message)

  private constructor(
    action: String,
    plugin: String,
    projectID: ProjectID,
    userID: UserID,
    datasetID: DatasetID,
    message: String,
    cause: Throwable,
  ) : super(action, plugin, projectID, userID, datasetID, message, cause)

  companion object {
    @JvmStatic
    fun import(plugin: String, userID: UserID, datasetID: DatasetID, message: String? = null, cause: Throwable? = null) =
      new("import", plugin, "N/A", userID, datasetID, message, cause)

    @JvmStatic
    fun installData(
      plugin: String,
      projectID: ProjectID,
      userID: UserID,
      datasetID: DatasetID,
      message: String? = null,
      cause: Throwable? = null,
    ) = new("install-data", plugin, projectID, userID, datasetID, message, cause)

    @JvmStatic
    fun installMeta(
      plugin: String,
      projectID: ProjectID,
      userID: UserID,
      datasetID: DatasetID,
      message: String? = null,
      cause: Throwable? = null,
    ) = new("install-meta", plugin, projectID, userID, datasetID, message, cause)

    @JvmStatic
    fun uninstall(
      plugin: String,
      projectID: ProjectID,
      userID: UserID,
      datasetID: DatasetID,
      message: String? = null,
      cause: Throwable? = null,
    ) = new("uninstall", plugin, projectID, userID, datasetID, message, cause)

    private fun new(
      action: String,
      plugin: String,
      projectID: ProjectID,
      userID: UserID,
      datasetID: DatasetID,
      message: String?,
      cause: Throwable?,
    ) =
      makeMessage(action, plugin, projectID, userID, datasetID, message, cause)
        .let {
          if (cause == null)
            PluginRequestException(action, plugin, projectID, userID, datasetID, it)
          else
            PluginRequestException(action, plugin, projectID, userID, datasetID, it, cause)
        }

    private fun makeMessage(
      action: String,
      plugin: String,
      projectID: ProjectID,
      userID: UserID,
      datasetID: DatasetID,
      message: String?,
      cause: Throwable?,
    ) =
      "error while making a(n) $action request to plugin $plugin for dataset $userID/$datasetID targeting project " +
        projectID +
        if (message != null)
          ": $message"
        else if (cause != null)
          ": ${cause.message}"
        else
          " for unknown reasons"
  }
}

