package vdi.core.metrics;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;

public final class UninstallMetrics {
  private static final Counter uninstallCount = Counter.build()
    .name("dataset_uninstallations")
    .help("Dataset uninstallation results.")
    .labelNames("type_name", "type_version", "code")
    .register();

  private static final Histogram durationHist = Histogram.build()
    .name("dataset_uninstallation_times")
    .help("Dataset uninstallation times in seconds.")
    .labelNames("type_name", "type_version")
    .buckets(
      5.0,   // 5 Seconds
      10.0,
      15.0,
      30.0,
      60.0,  // 1 Minute
      150.0, // 2.5 Minutes
      300.0, // 5 Minutes
      600.0, // 10 Minutes
      900.0, // 15 Minutes
      1800.0 // 30 Minutes
    )
    .register();

  private static final Gauge queueSize = Gauge.build()
    .name("soft_delete_queue_size")
    .help("Number of soft deletes currently queued to be processed.")
    .register();

  public static Counter uninstallCounter() {
    return uninstallCount;
  }

  public static Histogram durationHistogram() {
    return durationHist;
  }

  public static Gauge queueSizeGauge() {
    return queueSize;
  }
}
