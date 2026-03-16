package vdi.core.health;

import java.util.Map;

public interface Dependency {
  String getName();
  String getProtocol();
  String getHost();
  int getPort();
  Map<String, Object> getExtraFields();

  enum Status {
    OK,
    NOT_OK,
    UNKNOWN
  }

  Status checkStatus();

  default String urlString() {
    return this.getProtocol().isBlank()
      ? String.format("%s:%d", this.getHost(), this.getPort())
      : String.format("%s://%s:%d", this.getProtocol(), this.getHost(), this.getPort());
  }
}
