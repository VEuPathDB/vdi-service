package vdi.service.plugin

import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.micrometer.prometheusmetrics.PrometheusConfig
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import vdi.service.plugin.metrics.ScriptMetrics
import vdi.service.plugin.script.ScriptExecutorImpl
import vdi.config.loadAndCacheStackConfig
import vdi.logging.MetaLogger
import vdi.service.plugin.conf.ServiceConfiguration
import vdi.service.plugin.consts.ConfigDefault
import vdi.service.plugin.model.ApplicationContext
import vdi.service.plugin.model.MetricsBundle
import vdi.service.plugin.server.configureServer
import vdi.service.plugin.util.DatasetPathFactory

fun main() {
  MetaLogger.info("loading configuration")
  val config = loadAndCacheStackConfig().vdi
  val plugin = System.getenv("PLUGIN_ID")?.let(config.plugins::get)
    ?: throw IllegalStateException("could not match PLUGIN_ID to plugin configuration")

  val appCtx = ApplicationContext(
    ServiceConfiguration(plugin, config.siteBuild),
    ScriptExecutorImpl(),
    PrometheusMeterRegistry(PrometheusConfig.DEFAULT).let { MetricsBundle(it, ScriptMetrics(it)) },
    DatasetPathFactory(plugin.installRoot ?: "/datasets", config.siteBuild)
  )

  MetaLogger.info("starting http server")
  embeddedServer(Netty, plugin.server.port?.toInt() ?: ConfigDefault.ServerPort) { configureServer(appCtx) }
    .start(true)
}
