package vdi.model.api.internal

object Endpoint {
  const val InstallPathRoot = "/install"
  const val DataSubPath = "/data"
  const val MetaSubPath = "/meta"

  const val Import = "/import"
  const val InstallData = "$InstallPathRoot$DataSubPath"
  const val InstallMeta = "$InstallPathRoot$MetaSubPath"
  const val Uninstall = "/uninstall"

}