package vdi.core.plugin.client

import vdi.model.data.DatasetID
import vdi.model.data.InstallTargetID
import vdi.model.data.UserID

open class PluginException : Exception {
  val action: String
  val plugin: String
  val installTarget: InstallTargetID
  val userID: UserID
  val datasetID: DatasetID

  override val message: String
    get() = super.message!!

  protected constructor(
    action: String,
    plugin: String,
    installTarget: InstallTargetID,
    userID: UserID,
    datasetID: DatasetID,
    message: String,
  ) : super(message) {
    this.action = action
    this.plugin = plugin
    this.installTarget = installTarget
    this.userID = userID
    this.datasetID = datasetID
  }

  protected constructor(
    action: String,
    plugin: String,
    installTarget: InstallTargetID,
    userID: UserID,
    datasetID: DatasetID,
    message: String,
    cause: Throwable,
  ) : super(message, cause) {
    this.action = action
    this.plugin = plugin
    this.installTarget = installTarget
    this.userID = userID
    this.datasetID = datasetID
  }

  fun log(logFn: (String, Throwable) -> Unit) {
    logFn(message, this)
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
      installTarget: InstallTargetID,
      userID: UserID,
      datasetID: DatasetID,
      message: String? = null,
      cause: Throwable? = null,
    ) = new(
      "install-data",
      plugin,
      installTarget,
      userID,
      datasetID,
      message,
      cause
    )

    @JvmStatic
    fun installMeta(
      plugin: String,
      installTarget: InstallTargetID,
      userID: UserID,
      datasetID: DatasetID,
      message: String? = null,
      cause: Throwable? = null,
    ) = new(
      "install-meta",
      plugin,
      installTarget,
      userID,
      datasetID,
      message,
      cause
    )

    @JvmStatic
    fun uninstall(
      plugin: String,
      installTarget: InstallTargetID,
      userID: UserID,
      datasetID: DatasetID,
      message: String? = null,
      cause: Throwable? = null,
    ) = new(
      "uninstall",
      plugin,
      installTarget,
      userID,
      datasetID,
      message,
      cause
    )

    private fun new(
      action: String,
      plugin: String,
      installTarget: InstallTargetID,
      userID: UserID,
      datasetID: DatasetID,
      message: String?,
      cause: Throwable?,
    ) =
      makeMessage(
        action,
        plugin,
        installTarget,
        userID,
        datasetID,
        message,
        cause
      )
        .let {
          if (cause == null)
            PluginException(action, plugin, installTarget, userID, datasetID, it)
          else
            PluginException(action, plugin, installTarget, userID, datasetID, it, cause)
        }

    private fun makeMessage(
      action: String,
      plugin: String,
      installTarget: InstallTargetID,
      userID: UserID,
      datasetID: DatasetID,
      message: String? = null,
      cause: Throwable? = null,
    ) =
      "$action failed for dataset $userID/$datasetID in plugin $plugin targeting $installTarget" +
        if (message != null)
          ": $message"
        else if (cause != null)
          ": ${cause.message}"
        else
          " for unknown reasons"
  }
}

