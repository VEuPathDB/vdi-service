package vdi.core.metrics;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;

public final class InstallMetrics {
  private static final Counter installCount = Counter.build()
    .name("dataset_installations")
    .help("Dataset installation results.")
    .labelNames("type_name", "type_version", "code")
    .register();

  private static final Histogram durationHist = Histogram.build()
    .name("dataset_installation_times")
    .help("Dataset installation times in seconds.")
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
    .name("installation_queue_size")
    .help("Number of installations currently queued to be processed.")
    .register();

  public static Counter installCounter() {
    return installCount;
  }

  public static Histogram durationHistogram() {
    return durationHist;
  }

  public static Gauge queueSizeGauge() {
    return queueSize;
  }
}
