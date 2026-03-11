package vdi.core.kafka;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum EventSource {
  OBJECT_STORE,
  FULL_RECONCILER,
  SLIM_RECONCILER,
  UPDATE_META_LANE,
  INSTALL_DATA_LANE,
  ;

  @JsonCreator
  public static EventSource fromString(String value) {
    for (var e : EventSource.values())
      if (e.name().equals(value))
        return e;

    throw new IllegalArgumentException("unrecognized EventSource value: $value");
  }
}
