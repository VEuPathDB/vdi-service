package vdi.core.metrics;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;

public final class ImportMetrics {
  private static final Counter importCount = Counter.build()
    .name("dataset_imports")
    .help("Dataset import results.")
    .labelNames("type_name", "type_version", "code")
    .register();

  private static final Histogram durationHist = Histogram.build()
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
      30.0
    )
    .register();

  private static final Gauge queueSize = Gauge.build()
    .name("import_queue_size")
    .help("Number of imports currently queued to be processed.")
    .register();

  public static Counter importCounter() {
    return importCount;
  }

  public static Histogram durationHistogram() {
    return durationHist;
  }

  public static Gauge queueSizeGauge() {
    return queueSize;
  }
}
