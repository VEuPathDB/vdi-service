package vdi.service.rest.config;

import vdi.core.config.vdi.RestServiceConfig;

public record UploadConfig(long maxUploadSize, long userMaxStorageSize) {
  private static final long DefaultMaxUploadSize = 1073741824L;
  private static final long DefaultMaxStorageSize = 10737418240L;

  public UploadConfig(RestServiceConfig config) {
    this(
      config == null ? DefaultMaxUploadSize : config.getLongMaxUploadSize(),
      config == null ? DefaultMaxStorageSize : config.getLongUserMaxStorageSize()
    );
  }
}
