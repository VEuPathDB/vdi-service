package vdi.lane.reconciliation

import org.slf4j.Logger
import vdi.lane.reconciliation.util.require
import vdi.lane.reconciliation.util.safeExec
import vdi.lane.reconciliation.util.safeTest
import vdi.lib.db.cache.model.DatasetImportStatus
import vdi.lib.kafka.EventSource
import vdi.lib.s3.DatasetDirectory
import vdi.lib.s3.paths.S3File

internal class ReconciliationContext(
  val datasetDirectory: DatasetDirectory,
  val source: EventSource,
  val logger: Logger,
) {
  inline val userID
    get() = datasetDirectory.ownerID

  inline val datasetID
    get() = datasetDirectory.datasetID

  inline val datasetRef
    get() = "$userID/$datasetID"

  // eagerly load metadata to fail fast if absent
  val meta = safeExec("failed to load metadata") { datasetDirectory.getMetaFile().load() }
    .require(this, "missing ${S3File.Metadata} file")

  val manifest by lazy { safeExec("failed to load manifest") { datasetDirectory.getManifestFile().load() } }
  val hasManifest
    get() = manifest != null

  val hasDeleteFlag by lazy { safeTest(S3File.DeleteFlag) { datasetDirectory.hasDeleteFlag() } }

  val hasRevisedFlag by lazy { safeTest(S3File.RevisionFlag) {datasetDirectory.hasRevisedFlag() } }

  val hasRawUpload by lazy { safeTest(S3File.RawUploadZip) { datasetDirectory.hasUploadFile() } }

  val hasImportReadyData by lazy { safeTest(S3File.ImportReadyZip) { datasetDirectory.hasImportReadyFile() } }

  val hasInstallReadyData by lazy { safeTest(S3File.InstallReadyZip) { datasetDirectory.hasInstallReadyFile() } }

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
