package vdi.service.plugin

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import kotlin.io.path.Path
import vdi.service.plugin.metrics.ScriptMetrics
import vdi.service.plugin.script.ScriptExecutorImpl
import vdi.config.loadAndCastConfig
import vdi.config.loadManifestConfig
import vdi.logging.MetaLogger
import vdi.service.plugin.conf.PluginServerConfig
import vdi.service.plugin.conf.ServiceConfiguration
import vdi.service.plugin.consts.ConfigDefault
import vdi.service.plugin.model.ApplicationContext
import vdi.service.plugin.model.MetricsBundle
import vdi.service.plugin.server.configureServer
import vdi.service.plugin.util.DatasetPathFactory

fun main() {
  val manifest = loadManifestConfig()
  MetaLogger.info("=".repeat(80))
  MetaLogger.info("starting VDI plugin server version: {}", manifest.gitTag)

  val config = loadAndCastConfig<PluginServerConfig>(schema = Path("/schema/config/plugin-config.json")).vdi
  val plugin = System.getenv("PLUGIN_ID")?.let(config.plugins::get)
    ?: throw IllegalStateException("could not match PLUGIN_ID to plugin configuration")

  val appCtx = ApplicationContext(
    ServiceConfiguration(plugin, config.siteBuild),
    ScriptExecutorImpl(),
    PrometheusMeterRegistry(PrometheusConfig.DEFAULT).let { MetricsBundle(it, ScriptMetrics(it)) },
    DatasetPathFactory(plugin.installRoot ?: "/datasets", config.siteBuild)
  )

  MetaLogger.info("configuration loaded; starting netty")
  embeddedServer(Netty, plugin.server.port?.toInt() ?: ConfigDefault.ServerPort) { configureServer(appCtx) }
    .start(true)
}
