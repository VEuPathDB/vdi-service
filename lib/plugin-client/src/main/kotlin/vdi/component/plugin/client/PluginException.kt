package vdi.component.plugin.client

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID

class PluginException : Exception {
  val action: String
  val plugin: String
  val projectID: ProjectID
  val userID: UserID
  val datasetID: DatasetID

  constructor(
    action: String,
    plugin: String,
    projectID: ProjectID,
    userID: UserID,
    datasetID: DatasetID,
  ) : super(makeMessage(action, plugin, projectID, userID, datasetID)) {
    this.action = action
    this.plugin = plugin
    this.projectID = projectID
    this.userID = userID
    this.datasetID = datasetID
  }

  constructor(
    action: String,
    plugin: String,
    projectID: ProjectID,
    userID: UserID,
    datasetID: DatasetID,
    message: String,
  ) : super(makeMessage(action, plugin, projectID, userID, datasetID, message)) {
    this.action = action
    this.plugin = plugin
    this.projectID = projectID
    this.userID = userID
    this.datasetID = datasetID
  }

  constructor(
    action: String,
    plugin: String,
    projectID: ProjectID,
    userID: UserID,
    datasetID: DatasetID,
    cause: Throwable,
  ) : super(makeMessage(action, plugin, projectID, userID, datasetID, null, cause), cause) {
    this.action = action
    this.plugin = plugin
    this.projectID = projectID
    this.userID = userID
    this.datasetID = datasetID
  }

  constructor(
    action: String,
    plugin: String,
    projectID: ProjectID,
    userID: UserID,
    datasetID: DatasetID,
    message: String,
    cause: Throwable,
  ) : super(makeMessage(action, plugin, projectID, userID, datasetID, message), cause) {
    this.action = action
    this.plugin = plugin
    this.projectID = projectID
    this.userID = userID
    this.datasetID = datasetID
  }

  companion object {
    @JvmStatic
    fun import(
      plugin: String,
      userID: UserID,
      datasetID: DatasetID,
      message: String? = null,
      cause: Throwable? = null,
    ) = new("import", plugin, "N/A", userID, datasetID, message, cause)

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
    ) = when {
      message != null && cause != null -> PluginException(action, plugin, projectID, userID, datasetID, message, cause)
      message != null -> PluginException(action, plugin, projectID, userID, datasetID, message)
      cause != null -> PluginException(action, plugin, projectID, userID, datasetID, cause)
      else -> PluginException(action, plugin, projectID, userID, datasetID)
    }

    private fun makeMessage(
      action: String,
      plugin: String,
      projectID: ProjectID,
      userID: UserID,
      datasetID: DatasetID,
      message: String? = null,
      cause: Throwable? = null,
    ) =
      "$action failed for dataset $userID/$datasetID in plugin $plugin targeting $projectID" +
        if (message != null)
          ": $message"
        else if (cause != null)
          ": ${cause.message}"
        else
          " for unknown reasons"
  }
}

