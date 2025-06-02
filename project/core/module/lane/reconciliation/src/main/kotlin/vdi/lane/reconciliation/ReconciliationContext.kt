package vdi.lane.reconciliation

import org.slf4j.Logger
import vdi.lane.reconciliation.util.require
import vdi.lane.reconciliation.util.safeExec
import vdi.lane.reconciliation.util.safeTest
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.core.kafka.EventSource
import vdi.core.s3.DatasetDirectory
import vdi.core.s3.files.FileName

internal class ReconciliationContext(
  val datasetDirectory: DatasetDirectory,
  val source: EventSource,
  val logger: Logger,
) {
  inline val userID
    get() = datasetDirectory.ownerID

  inline val datasetID
    get() = datasetDirectory.datasetID

  // eagerly load metadata to fail fast if absent
  val meta = safeExec("failed to load metadata") { datasetDirectory.getMetaFile().load() }
    .require(this, "missing ${FileName.MetadataFile} file")

  val manifest by lazy { safeExec("failed to load manifest") { datasetDirectory.getManifestFile().load() } }
  val hasManifest
    get() = manifest != null

  val hasDeleteFlag by lazy { safeTest(FileName.DeleteFlagFile) { datasetDirectory.hasDeleteFlag() } }

  val hasRevisedFlag by lazy { safeTest(FileName.RevisedFlagFile) {datasetDirectory.hasRevisedFlag() } }

  val hasRawUpload by lazy { safeTest(FileName.RawUploadFile) { datasetDirectory.hasUploadFile() } }

  val hasImportReadyData by lazy { safeTest(FileName.ImportReadyFile) { datasetDirectory.hasImportReadyFile() } }

  val hasInstallReadyData by lazy { safeTest(FileName.InstallReadyFile) { datasetDirectory.hasInstallReadyFile() } }

  var importControl: DatasetImportStatus? = null

  var hasImportMessages: Boolean? = null

  var haveFiredMetaUpdateEvent = false
  var haveFiredImportEvent = false
  var haveFiredInstallEvent = false
  var haveFiredShareEvent = false
  var haveFiredUninstallEvent = false

  inline val haveFiredAnyEvents
    get() = haveFiredMetaUpdateEvent
      || haveFiredImportEvent
      || haveFiredInstallEvent
      || haveFiredShareEvent
      || haveFiredUninstallEvent
}
