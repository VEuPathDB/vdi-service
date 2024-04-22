package vdi.component.db.app.model

data class InstallStatuses(
  var meta: InstallStatus? = null,
  var metaMessage: String? = null,
  var data: InstallStatus? = null,
  var dataMessage: String? = null,
)