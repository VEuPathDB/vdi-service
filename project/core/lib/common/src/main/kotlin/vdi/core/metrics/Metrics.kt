package vdi.core.metrics

import io.prometheus.client.Counter
import io.prometheus.client.Gauge
import io.prometheus.client.Histogram

fun Counter.initZeroes(): Counter = run {
  this.inc(0.0)
  return this
}

object Metrics {

  object Reinstaller {
    val failedReinstallProcessForProject: Counter = Counter.build()
      .name("failed_reinstall_process")
      .labelNames("project")
      .help("The re-install process fails at project level")
      .register()

    val failedDatasetReinstall: Counter = Counter.build()
      .name("failed_dataset_reinstall")
      .help("A dataset fails to be re-installed.")
      .register()
      .initZeroes()

    val reinstallationTimes: Histogram = Histogram.build()
      .name("dataset_reinstallation_times")
      .help("Dataset reinstallation run times.")
      .buckets(
        1.0,
        5.0,
        15.0,
        30.0,
        60.0,
        150.0,
        300.0,
      )
      .register()
  }

  val shareQueueSize: Gauge = Gauge.build()
    .name("share_queue_size")
    .help("Number of shares currently queued to be processed.")
    .register()

  val malformedDatasetFound: Counter = Counter.build()
    .name("malformed_dataset_found")
    .help("A Malformed dataset was found during reconciliation.")
    .register()

  object Upload {
    val failed: Counter = Counter.build()
      .name("upload_failed")
      .help("An upload failed with an unexpected exception.")
      .register()
      .initZeroes()

    val queueSize: Gauge = Gauge.build()
      .name("dataset_upload_queue_size")
      .help("Number of dataset uploads currently queued to be processed.")
      .register()
  }

  object Reconciler {

    object Slim {
      val datasetsSynced: Counter = Counter.build()
        .name("slim_reconciler_synced")
        .help("Number of sync events fired by the slim reconciliation process.")
        .register()

      val executionTime: Histogram = Histogram.build()
        .name("slim_reconciler_times")
        .help("Slim reconciler execution times.")
        .register()

      val failures: Counter = Counter.build()
        .name("slim_reconciler_failures")
        .help("Slim reconciler execution failures.")
        .register()
    }

    object Full {
      val reconcilerDatasetDeleted: Counter = Counter.build()
        .name("dataset_reconciler_deleted")
        .help("Count of datasets deleted by reconciler.")
        .labelNames("target_name")
        .register()

      val reconcilerDatasetSynced: Counter = Counter.build()
        .name("dataset_reconciler_synced")
        .help("Count of datasets synced by reconciler.")
        .labelNames("target_name")
        .register()

      val failedReconciliation: Counter = Counter.build()
        .name("dataset_reconciler_failed")
        .help("Count of failed reconciler runs.")
        .labelNames("target_name")
        .register()

      val missingInTarget: Counter = Counter.build()
        .name("dataset_reconciler_missing_in_target")
        .help("Count of datasets the reconciler finds are missing in the target DB.")
        .labelNames("target_name")
        .register()

      val reconcilerTimes: Histogram = Histogram.build()
        .name("dataset_reconciler_times")
        .help("Dataset reconciler run times.")
        .buckets(
          30.0,
          60.0,  // 1 minute
          150.0, // 2.5 minutes
          300.0, // 5 minutes
          600.0, // 10 minutes
          900.0, // 15 minutes
        )
        .register()
    }
  }

  object ReconciliationHandler {
    val queueSize: Gauge = Gauge.build()
      .name("reconciliation_handler_queue_size")
      .help("Number of dataset reconciliations currently queued to be processed.")
      .register()

    val warnings: Counter = Counter.build()
      .name("reconciliation_handler_warning_counter")
      .help("Number of warnings encountered by the dataset reconciler.")
      .register()
      .initZeroes()

    val errors: Counter = Counter.build()
      .name("reconciliation_handler_error_counter")
      .help("Number of errors encountered by the dataset reconciler.")
      .register()
      .initZeroes()
  }
}
