package vdi.service.plugin.script

import java.nio.file.Path
import kotlin.time.Duration

interface PluginScript {
  val kind: Kind

  val path: Path

  val maxDuration: Duration

  enum class Kind {
    CheckCompatibility,
    Import,
    InstallData,
    InstallMeta,
    Uninstall,
  }
}
