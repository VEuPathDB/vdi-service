package org.veupathdb.service.vdi.model

enum class InstallStatus(val value: String) {
  Running("running"),
  Complete("complete"),
  FailedValidation("failed-validation"),
  FailedInstallation("failed-installation"),
  ReadyForReinstall("ready-for-reinstall"),
  ;

  companion object {
    @JvmStatic
    fun fromString(value: String): InstallStatus {
      for (enum in values())
        if (enum.value == value)
          return enum

      throw IllegalArgumentException("unrecognized InstallStatus value: $value")
    }
  }
}

