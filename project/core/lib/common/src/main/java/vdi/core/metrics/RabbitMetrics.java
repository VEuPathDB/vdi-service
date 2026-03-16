package vdi.core.metrics;

import io.prometheus.client.Counter;

public final class RabbitMetrics {
  private static long lastMessageReceived      = 0;

  private static final Counter unparseableRabbitMessage = Counter.build()
    .name("unparseable_rabbit_message")
    .help("A rabbit message is unparseable.")
    .register();

  static {
    unparseableRabbitMessage.inc(0.0);
  }

  public static long getLastMessageReceived() {
    return lastMessageReceived;
  }

  public static void setLastMessageReceived(long lastMessageRecieved) {
    RabbitMetrics.lastMessageReceived = lastMessageRecieved;
  }

  public static Counter unparseableRabbitMessageCounter() {
    return unparseableRabbitMessage;
  }
}
