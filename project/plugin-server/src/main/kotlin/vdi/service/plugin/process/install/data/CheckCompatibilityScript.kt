package vdi.service.plugin.process.install.data

import java.nio.file.Path
import kotlin.time.Duration
import vdi.service.plugin.script.ExitStatus
import vdi.service.plugin.script.PluginScript

internal class CheckCompatibilityScript(
  override val path: Path,
  override val maxDuration: Duration,
): PluginScript {
  override val kind: PluginScript.Kind
    get() = PluginScript.Kind.CheckCompatibility

  object ExitCode {
    inline val Success get() = ExitStatus.Success
    inline val CompatibilityError get() = ExitStatus.CompatibilityError

    fun fromCode(code: Int): ExitStatus.Info =
      when (val c = code.toUByte()) {
        ExitStatus.Success.code            -> ExitStatus.Success
        ExitStatus.CompatibilityError.code -> ExitStatus.CompatibilityError
        else                               -> ExitStatus.Unknown(c)
      }
  }
}