package vdi.service.plugin.model

import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import vdi.service.plugin.metrics.ScriptMetrics

data class MetricsBundle(
  val micrometer:    PrometheusMeterRegistry,
  val scriptMetrics: ScriptMetrics,
)
