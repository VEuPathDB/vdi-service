package vdi.core.metrics;

import io.prometheus.client.Counter;
import io.prometheus.client.Histogram;

public final class PrunerMetrics {
  private static final Histogram duration = Histogram.build()
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
        60.0
    )
    .register();

  private static final Counter failed = Counter.build()
    .name("prune_failed")
    .help("A dataset fails to be pruned.")
    .register();

  private static final Counter success = Counter.build()
    .name("prune_successful")
    .help("A dataset is successfully pruned.")
    .register();

  private static final Counter conflicts = Counter.build()
    .name("prune_conflict")
    .help("Two or more data stores disagree on whether a dataset is marked as deleted.")
    .register();

  static {
    failed.inc(0.0);
    success.inc(0.0);
    conflicts.inc(0.0);
  }

  public static Histogram durationHistogram() {
    return duration;
  }

  public static Counter failedCounter() {
    return failed;
  }

  public static Counter successCounter() {
    return success;
  }

  public static Counter conflictCounter() {
    return conflicts;
  }
}
