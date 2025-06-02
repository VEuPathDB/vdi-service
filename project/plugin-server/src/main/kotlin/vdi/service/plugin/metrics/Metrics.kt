package vdi.service.plugin.metrics

import io.micrometer.prometheusmetrics.PrometheusMeterRegistry
import io.prometheus.metrics.core.metrics.Counter
import io.prometheus.metrics.core.metrics.Histogram
import io.prometheus.metrics.model.snapshots.Unit

class ScriptMetrics(registry: PrometheusMeterRegistry) {

  val importScriptDuration: Histogram = Histogram.builder()
    .name("script_import_duration")
    .help("Import script duration in seconds.")
    .unit(Unit.SECONDS)
    .register(registry.prometheusRegistry)

  val importScriptCalls: Counter = Counter.builder()
    .name("import_script_executions")
    .help("Import script executions by exit code.")
    .labelNames("exit_status")
    .register(registry.prometheusRegistry)

  val installMetaScriptDuration: Histogram = Histogram.builder()
    .name("script_install_meta_duration")
    .help("Install-Meta script duration in seconds")
    .unit(Unit.SECONDS)
    .register(registry.prometheusRegistry)

  val installMetaCalls: Counter = Counter.builder()
    .name("install_meta_script_executions")
    .help("Install-Meta script executions by exit code.")
    .labelNames("exit_status")
    .register(registry.prometheusRegistry)

  val installDataScriptDuration: Histogram = Histogram.builder()
    .name("script_install_data_duration")
    .help("Install-Data script duration in seconds")
    .unit(Unit.SECONDS)
    .register(registry.prometheusRegistry)

  val installDataCalls: Counter = Counter.builder()
    .name("install_data_script_executions")
    .help("Install-Data script executions by exit code.")
    .labelNames("exit_status")
    .register(registry.prometheusRegistry)

  val checkCompatScriptDuration: Histogram = Histogram.builder()
    .name("script_check_compat_duration")
    .help("Check-Compatibility script duration in seconds")
    .unit(Unit.SECONDS)
    .register(registry.prometheusRegistry)

  val checkCompatCalls: Counter = Counter.builder()
    .name("check_compat_script_executions")
    .help("Check-Compatibility script executions by exit code.")
    .labelNames("exit_status")
    .register(registry.prometheusRegistry)

  val uninstallScriptDuration: Histogram = Histogram.builder()
    .name("script_uninstall_duration")
    .help("Uninstall script duration in seconds")
    .unit(Unit.SECONDS)
    .register(registry.prometheusRegistry)

  val uninstallCalls: Counter = Counter.builder()
    .name("uninstall_script_executions")
    .help("Uninstall script executions by exit code.")
    .labelNames("exit_status")
    .register(registry.prometheusRegistry)
}
