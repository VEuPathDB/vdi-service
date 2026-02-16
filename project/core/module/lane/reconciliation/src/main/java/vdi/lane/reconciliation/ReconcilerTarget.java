package vdi.lane.reconciliation;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import vdi.core.db.cache.model.DatasetImportStatus;
import vdi.core.kafka.EventSource;
import vdi.core.s3.DatasetDirectory;
import vdi.core.s3.files.FileName;
import vdi.model.meta.DatasetManifest;
import vdi.model.meta.DatasetMetadata;
import vdi.model.meta.UserID;

import java.util.function.Supplier;

public class ReconcilerTarget {
  @NotNull
  private final String datasetId;

  @NotNull
  public final DatasetDirectory datasetDirectory;

  @NotNull
  public final EventSource source;

  @NotNull
  public final Logger logger;

  @Nullable
  private DatasetMetadata meta;
  private boolean loadedMeta;

  @Nullable
  private DatasetManifest manifest;
  private boolean loadedManifest;

  @Nullable
  public DatasetImportStatus importControl;

  @Nullable
  public Boolean hasImportMessages;

  public boolean haveFiredMetaUpdateEvent;
  public boolean haveFiredImportEvent;
  public boolean haveFiredInstallEvent;
  public boolean haveFiredShareEvent;
  public boolean haveFiredUninstallEvent;

  public ReconcilerTarget(
    @NotNull String datasetId,
    @NotNull DatasetDirectory datasetDirectory,
    @NotNull EventSource source,
    @NotNull Logger logger
  ) {
    this.datasetId = datasetId;
    this.datasetDirectory = datasetDirectory;
    this.source = source;
    this.logger = logger;
  }

  @NotNull
  public UserID getUserId() {
    return datasetDirectory.getOwnerID();
  }

  @NotNull
  public String getDatasetId() {
    return datasetId;
  }

  @Nullable
  public DatasetManifest getManifest() {
    loadManifest();
    return manifest;
  }

  public boolean hasManifest() {
    loadManifest();
    return manifest != null;
  }

  @Nullable
  public DatasetMetadata getMeta() {
    loadMeta();
    return meta;
  }

  public boolean hasMeta() {
    loadMeta();
    return meta != null;
  }

  public boolean hasDeleteFlag() {
    return safeTest(FileName.DeleteFlagFile, datasetDirectory::hasDeleteFlag);
  }

  public boolean hasRevisedFlag() {
    return safeTest(FileName.RevisedFlagFile, datasetDirectory::hasRevisedFlag);
  }

  public boolean hasRawUpload() {
    return safeTest(FileName.RawUploadFile, datasetDirectory::hasUploadFile);
  }

  public boolean hasImportReadyData() {
    return safeTest(FileName.ImportReadyFile, datasetDirectory::hasImportReadyFile);
  }

  public boolean hasInstallReadyData() {
    return safeTest(FileName.InstallReadyFile, datasetDirectory::hasInstallReadyFile);
  }

  public boolean hasUploadError() {
    return safeTest(FileName.UploadErrorFile, datasetDirectory::hasUploadErrorFile);
  }

  public boolean haveFiredAnyEvents() {
    return haveFiredMetaUpdateEvent
      || haveFiredImportEvent
      || haveFiredInstallEvent
      || haveFiredShareEvent
      || haveFiredUninstallEvent;
  }

  private void loadManifest() {
    if (!loadedManifest) {
      manifest = safeExec("failed to load manifest", () -> datasetDirectory.getManifestFile().load());
      loadedManifest = true;
    }
  }

  private void loadMeta() {
    if (!loadedMeta) {
      meta = safeExec("failed to load metadata", () -> datasetDirectory.getMetaFile().load());
      loadedMeta = true;
    }
  }

  public boolean safeTest(String file, Supplier<Boolean> fn) {
    return safeExec("failed to test for file \"" + file + "\"", fn);
  }

  public <T> T safeExec(String message, Supplier<T> fn) {
    try {
      return fn.get();
    } catch (CriticalReconciliationError e) {
      throw e;
    } catch (Throwable e) {
      logger.error(message, e);
      throw new CriticalReconciliationError();
    }
  }
}
