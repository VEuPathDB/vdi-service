package vdi.service.plugin.service

import org.slf4j.Logger
import vdi.io.plugin.responses.ValidationResponse
import vdi.service.plugin.metrics.ScriptMetrics
import vdi.service.plugin.script.ScriptExecutor
import vdi.service.plugin.script.cloneEnvironment
import vdi.model.Environment
import vdi.service.plugin.consts.ScriptEnvKey
import vdi.service.plugin.server.context.ScriptContext

abstract class AbstractScriptHandler<T, C: ScriptContext<*>>(
  protected val scriptContext: C,
  protected val executor: ScriptExecutor,
  protected val metrics: ScriptMetrics,
  protected val logger: Logger,
) {
  protected inline val datasetID
    get() = scriptContext.datasetID

  protected inline val workspace
    get() = scriptContext.workspace

  protected inline val customPath
    get() = scriptContext.customPath

  suspend fun run(): T {
    logger.info("beginning dataset processing")
    return runJob()
      .also { logger.info("dataset processing completed") }
  }

  protected fun buildScriptEnv(): Environment {
    val out = cloneEnvironment()

    if (customPath.isNotBlank())
      out["PATH"] = out["PATH"] + ':' + customPath

    out[ScriptEnvKey.DatasetID] = datasetID.toString()

    appendScriptEnv(out)

    return out
  }

  protected open fun appendScriptEnv(env: MutableMap<String, String>) {}

  protected abstract suspend fun runJob(): T

  protected fun newValidationResponse(isValid: Boolean, warnings: Collection<String>): ValidationResponse {
    val basic = ArrayList<String>(warnings.size)
    val community = ArrayList<String>(warnings.size)

    warnings.forEach { when {
      it.startsWith(BasicValidationPrefix) -> basic.add(it.substring(BasicValidationPrefix.length).trim())
      it.startsWith(CommunityValidationPrefix) -> community.add(it.substring(CommunityValidationPrefix.length).trim())
      else -> basic.add(it.trim())
    } }

    return ValidationResponse(isValid, basic, community)
  }
}
