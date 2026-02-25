package vdi.service.plugin.script

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.files.Path
import vdi.service.plugin.util.toJavaPath

class ScriptExecutorImpl : ScriptExecutor {
  override suspend fun <T> executeScript(
    command: Path,
    workDir:     Path,
    arguments:   Array<String>,
    environment: Map<String, String>,
    fn:          suspend ScriptProcess.() -> T,
  ): T = withContext(Dispatchers.IO) {
    val rawProcess = ProcessBuilder(command.toString(), *arguments).apply {
      directory(workDir.toJavaPath().toFile())
      environment().clear()
      environment().putAll(environment)
    }.start()

    val out = try {
      fn(ScriptProcessImpl(command.toString(), rawProcess))
    } finally {
      rawProcess.waitFor()
    }

    out
  }
}