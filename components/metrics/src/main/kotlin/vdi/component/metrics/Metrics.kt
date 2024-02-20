package vdi.component.metrics

import io.prometheus.client.Counter
import io.prometheus.client.Gauge
import io.prometheus.client.Histogram

fun Counter.initZeroes(): Counter = run {
  this.inc(0.0)
  return this
}

object Metrics {

  object Import {
    val count: Counter = Counter.build()
      .name("dataset_imports")
      .help("Dataset import results.")
      .labelNames("type_name", "type_version", "code")
      .register()

    val duration: Histogram = Histogram.build()
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

    val queueSize: Gauge = Gauge.build()
      .name("import_queue_size")
      .help("Number of imports currently queued to be processed.")
      .register()
  }

  object Install {
    val count: Counter = Counter.build()
      .name("dataset_installations")
      .help("Dataset installation results.")
      .labelNames("type_name", "type_version", "code")
      .register()

    val duration: Histogram = Histogram.build()
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

    val queueSize: Gauge = Gauge.build()
      .name("install_queue_size")
      .help("Number of installs currently queued to be processed.")
      .register()
  }

  object Uninstall {
    val count: Counter = Counter.build()
      .name("dataset_uninstallations")
      .help("Dataset uninstallation results.")
      .labelNames("type_name", "type_version", "code")
      .register()

    val duration: Histogram = Histogram.build()
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
  }

  object MetaUpdates {
    val count: Counter = Counter.build()
      .name("dataset_meta_updates")
      .help("Dataset meta update times.")
      .labelNames("type_name", "type_version", "code")
      .register()

    val duration: Histogram = Histogram.build()
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
  }

  object Pruner {
    val duration: Histogram = Histogram.build()
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

    val failed: Counter = Counter.build()
      .name("prune_failed")
      .help("A dataset fails to be pruned.")
      .register()
      .initZeroes()

    val count: Counter = Counter.build()
      .name("prune_successful")
      .help("A dataset is successfully pruned.")
      .register()
      .initZeroes()

    val conflict: Counter = Counter.build()
      .name("prune_conflict")
      .help("Two or more data stores disagree on whether a dataset is marked as deleted.")
      .register()
      .initZeroes()
  }

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

  val updateMetaQueueSize: Gauge = Gauge.build()
    .name("update_meta_queue_size")
    .help("Number of meta updates currently queued to be processed.")
    .register()

  val softDeleteQueueSize: Gauge = Gauge.build()
    .name("soft_delete_queue_size")
    .help("Number of soft deletes currently queued to be processed.")
    .register()

  val unparseableRabbitMessage: Counter = Counter.build()
    .name("unparseable_rabbit_message")
    .help("A rabbit message is unparseable.")
    .register()
    .initZeroes()

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
      .name("dataset_reconiler_failed")
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
        5.0,
        15.0,
        30.0,
        60.0,  // 1 minute
        150.0, // 2.5 minutes
        300.0, // 5 minutes
        600.0, // 10 minutes
        900.0, // 15 minutes
      )
      .register()
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