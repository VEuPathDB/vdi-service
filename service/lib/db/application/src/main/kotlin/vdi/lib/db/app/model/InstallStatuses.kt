package vdi.lib.db.app.model

data class InstallStatuses(
  var meta: InstallStatus = InstallStatus.Running,
  var metaMessage: String? = null,
  var data: InstallStatus? = null,
  var dataMessage: String? = null,
)
