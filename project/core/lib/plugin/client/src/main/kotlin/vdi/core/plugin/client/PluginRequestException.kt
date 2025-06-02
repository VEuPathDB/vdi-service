package vdi.lib.plugin.client

import vdi.model.data.DatasetID
import vdi.model.data.InstallTargetID
import vdi.model.data.UserID

class PluginRequestException : PluginException {
  private constructor(
    action: String,
    plugin: String,
    installTarget: InstallTargetID,
    userID: UserID,
    datasetID: DatasetID,
    message: String,
  ) : super(action, plugin, installTarget, userID, datasetID, message)

  private constructor(
    action: String,
    plugin: String,
    installTarget: InstallTargetID,
    userID: UserID,
    datasetID: DatasetID,
    message: String,
    cause: Throwable,
  ) : super(action, plugin, installTarget, userID, datasetID, message, cause)

  companion object {
    @JvmStatic
    fun import(plugin: String, userID: UserID, datasetID: DatasetID, message: String? = null, cause: Throwable? = null) =
      new("import", plugin, "N/A", userID, datasetID, message, cause)

    @JvmStatic
    fun installData(
      plugin: String,
      installTarget: InstallTargetID,
      userID: UserID,
      datasetID: DatasetID,
      message: String? = null,
      cause: Throwable? = null,
    ) = new("install-data", plugin, installTarget, userID, datasetID, message, cause)

    @JvmStatic
    fun installMeta(
      plugin: String,
      installTarget: InstallTargetID,
      userID: UserID,
      datasetID: DatasetID,
      message: String? = null,
      cause: Throwable? = null,
    ) = new("install-meta", plugin, installTarget, userID, datasetID, message, cause)

    @JvmStatic
    fun uninstall(
      plugin: String,
      installTarget: InstallTargetID,
      userID: UserID,
      datasetID: DatasetID,
      message: String? = null,
      cause: Throwable? = null,
    ) = new("uninstall", plugin, installTarget, userID, datasetID, message, cause)

    private fun new(
      action: String,
      plugin: String,
      installTarget: InstallTargetID,
      userID: UserID,
      datasetID: DatasetID,
      message: String?,
      cause: Throwable?,
    ) =
      makeMessage(action, plugin, installTarget, userID, datasetID, message, cause)
        .let {
          if (cause == null)
            PluginRequestException(action, plugin, installTarget, userID, datasetID, it)
          else
            PluginRequestException(action, plugin, installTarget, userID, datasetID, it, cause)
        }

    private fun makeMessage(
      action: String,
      plugin: String,
      installTarget: InstallTargetID,
      userID: UserID,
      datasetID: DatasetID,
      message: String?,
      cause: Throwable?,
    ) =
      "error while making a(n) $action request to plugin $plugin for dataset $userID/$datasetID targeting project " +
        installTarget +
        if (message != null)
          ": $message"
        else if (cause != null)
          ": ${cause.message}"
        else
          " for unknown reasons"
  }
}

