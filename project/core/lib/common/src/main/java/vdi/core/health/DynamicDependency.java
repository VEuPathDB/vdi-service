package vdi.core.health;

import java.util.Map;
import java.util.function.Supplier;

public class DynamicDependency implements Dependency {
  private final String name;
  private final String protocol;
  private final String host;
  private final int port;
  private final Supplier<Map<String, Object>> extraSupplier;

  public DynamicDependency(
    String name,
    String protocol,
    String host,
    int port,
    Supplier<Map<String, Object>> extraSupplier
  ) {
    this.name = name;
    this.protocol = protocol;
    this.host = host;
    this.port = port;
    this.extraSupplier = extraSupplier;
  }

  @Override
  public String getName() {
    return this.name;
  }

  @Override
  public String getProtocol() {
    return this.protocol;
  }

  @Override
  public String getHost() {
    return this.host;
  }

  @Override
  public int getPort() {
    return this.port;
  }

  @Override
  public Map<String, Object> getExtraFields() {
    return this.extraSupplier.get();
  }

  @Override
  public Status checkStatus() {
    return Status.OK;
  }

  @Override
  public String toString() {
    return this.urlString();
  }
}
