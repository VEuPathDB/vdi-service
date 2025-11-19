package vdi.service.plugin.process.uninstall

import java.nio.file.Path
import kotlin.time.Duration
import vdi.service.plugin.script.ExitStatus
import vdi.service.plugin.script.PluginScript

class UninstallScript(
  override val path: Path,
  override val maxDuration: Duration,
): PluginScript {
  override val kind: PluginScript.Kind
    get() = PluginScript.Kind.Uninstall

  object ExitCode {
    inline val Success get() = ExitStatus.Success

    fun fromCode(code: Int): ExitStatus.Info =
      when (val c = code.toUByte()) {
        ExitStatus.Success.code -> ExitStatus.Success
        else                    -> ExitStatus.Unknown(c)
      }
  }
}