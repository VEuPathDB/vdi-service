package vdi.core.metrics;

import io.prometheus.client.Counter;
import io.prometheus.client.Gauge;
import io.prometheus.client.Histogram;

public final class UpdateMetaMetrics {
  private static final Counter updateMetaCount = Counter.build()
    .name("dataset_meta_updates")
    .help("Dataset meta update times.")
    .labelNames("type_name", "type_version", "code")
    .register();

  private static final Histogram durationHist = Histogram.build()
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
      60.0,  // 1 Minute
      150.0  // 2.5 Minutes
    )
    .register();

  private static final Gauge queueSize = Gauge.build()
    .name("update_meta_queue_size")
    .help("Number of meta updates currently queued to be processed.")
    .register();

  public static Counter updateMetaCounter() {
    return updateMetaCount;
  }

  public static Histogram durationHistogram() {
    return durationHist;
  }

  public static Gauge queueSizeGauge() {
    return queueSize;
  }
}
