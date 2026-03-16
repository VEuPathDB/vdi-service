package vdi.core.health;

import java.util.Map;

public class StaticDependency implements Dependency {
  private final String name;
  private final String protocol;
  private final String host;
  private final int port;
  private final Map<String, Object> extra;

  public StaticDependency(String name, String protocol, String host, int port, Map<String, Object> extra) {
    this.name = name;
    this.protocol = protocol;
    this.host = host;
    this.port = port;
    this.extra = extra;
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
    return this.extra;
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
