package vdi.component.metrics

import io.prometheus.client.Counter
import io.prometheus.client.Histogram

object Metrics {
  val imports: Counter = Counter.build()
    .name("dataset_imports")
    .help("Dataset import results.")
    .labelNames("type_name", "type_version", "code")
    .register()

  val importTimes: Histogram = Histogram.build()
    .name("dataset_import_times")
    .help("Dataset import times in seconds.")
    .labelNames("type_name", "type_version")
    .buckets(
      0.1,
      0.25,
      0.5,
      1.0,
      2.5,
      5.0,
      10.0,
      15.0,
      30.0,
    )
    .register()

  val installations: Counter = Counter.build()
    .name("dataset_installations")
    .help("Dataset installation results.")
    .labelNames("type_name", "type_version", "code")
    .register()

  val installationTimes: Histogram = Histogram.build()
    .name("dataset_installation_times")
    .help("Dataset installation times in seconds.")
    .labelNames("type_name", "type_version")
    .buckets(
      5.0,    // 5 Seconds
      10.0,
      15.0,
      30.0,
      60.0,   // 1 Minute
      150.0,  // 2.5 Minutes
      300.0,  // 5 Minutes
      600.0,  // 10 Minutes
      900.0,  // 15 Minutes
      1800.0, // 30 Minutes
    )
    .register()

  val uninstallations: Counter = Counter.build()
    .name("dataset_uninstallations")
    .help("Dataset uninstallation results.")
    .labelNames("type_name", "type_version", "code")
    .register()

  val uninstallationTimes: Histogram = Histogram.build()
    .name("dataset_uninstallation_times")
    .help("Dataset uninstallation times in seconds.")
    .labelNames("type_name", "type_version")
    .buckets(
      5.0,    // 5 Seconds
      10.0,
      15.0,
      30.0,
      60.0,   // 1 Minute
      150.0,  // 2.5 Minutes
      300.0,  // 5 Minutes
      600.0,  // 10 Minutes
      900.0,  // 15 Minutes
      1800.0, // 30 Minutes
    )
    .register()

  val metaUpdates: Counter = Counter.build()
    .name("dataset_meta_updates")
    .help("Dataset meta update times.")
    .labelNames("type_name", "type_version", "code")
    .register()

  val metaUpdateTimes: Histogram = Histogram.build()
    .name("dataset_meta_update_times")
    .help("Dataset meta update times in seconds.")
    .labelNames("type_name", "type_version")
    .buckets(
      1.0,
      2.5,
      5.0,
      10.0,
      15.0,
      30.0,
      60.0,   // 1 Minute
      150.0,  // 2.5 Minutes
    )
    .register()

  val pruneTimes: Histogram = Histogram.build()
    .name("prune_times")
    .help("Dataset pruner run times.")
    .buckets(
      0.25,
      0.5,
      1.0,
      2.5,
      5.0,
      10.0,
      15.0,
      30.0,
      60.0,
    )
    .register()
}