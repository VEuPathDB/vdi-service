package vdi.lane.imports;

import java.util.function.BiConsumer;

public class ImportException extends RuntimeException {
  private final long userId;
  private final String datasetId;

  public ImportException(long userId, String datasetId, Throwable cause) {
    super(String.format("import failed for dataset %d/%s", userId, datasetId), cause);
    this.userId = userId;
    this.datasetId = datasetId;
  }

  public long getUserId() {
    return userId;
  }

  public String getDatasetId() {
    return datasetId;
  }

  public void log(BiConsumer<String, Throwable> logFn) {
    logFn.accept(getMessage(), this);
  }
}
