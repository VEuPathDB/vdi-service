package vdi.service.plugin.model

import vdi.service.plugin.script.ScriptExecutor
import vdi.service.plugin.conf.ServiceConfiguration
import vdi.service.plugin.util.DatasetPathFactory

data class ApplicationContext(
  val config:      ServiceConfiguration,
  val executor:    ScriptExecutor,
  val metrics:     MetricsBundle,
  val pathFactory: DatasetPathFactory
)
