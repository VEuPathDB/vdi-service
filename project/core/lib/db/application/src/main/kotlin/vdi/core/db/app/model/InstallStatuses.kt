package vdi.core.db.app.model

data class InstallStatuses(
  var meta: InstallStatus = InstallStatus.Running,
  var metaMessages: List<String> = emptyList(),
  var data: InstallStatus? = null,
  var dataMessages: List<String> = emptyList(),
)
