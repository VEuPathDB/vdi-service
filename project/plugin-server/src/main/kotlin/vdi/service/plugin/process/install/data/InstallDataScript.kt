package vdi.service.plugin.process.install.data

import java.nio.file.Path
import kotlin.time.Duration
import vdi.service.plugin.script.ExitStatus
import vdi.service.plugin.script.PluginScript

internal class InstallDataScript(
  override val path: Path,
  override val maxDuration: Duration,
): PluginScript {
  override val kind: PluginScript.Kind
    get() = PluginScript.Kind.InstallData

  object ExitCode {
    inline val Success get() = ExitStatus.Success
    inline val ValidationError get() = ExitStatus.ValidationError

    fun fromCode(code: Int): ExitStatus.Info =
      when (val c = code.toUByte()) {
        ExitStatus.Success.code         -> ExitStatus.Success
        ExitStatus.ValidationError.code -> ExitStatus.ValidationError
        else                            -> ExitStatus.Unknown(c)
      }
  }
}