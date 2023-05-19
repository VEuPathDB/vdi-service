package vdi.component.pruner

import org.slf4j.LoggerFactory
import org.veupathdb.lib.s3.s34k.S3Api
import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.db.app.AppDB
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.db.cache.model.DeletedDataset
import org.veupathdb.vdi.lib.s3.datasets.DatasetManager
import org.veupathdb.vdi.lib.s3.datasets.paths.S3Paths
import java.time.OffsetDateTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

object Pruner {
  private val log = LoggerFactory.getLogger(javaClass)

  private val lock = ReentrantLock()

  private val config = PrunerConfig()

  fun pruneDatasets() {
    log.info("starting dataset pruning job")
    lock.withLock { doPruning() }
  }

  fun canPrune() = !lock.isLocked

  private fun doPruning() {
    val s3Client = S3Api.newClient(config.s3Config)
    val s3Bucket = s3Client.buckets[config.bucketName]
      ?: throw IllegalStateException("bucket ${config.bucketName} does not exist!")

    val dsm = DatasetManager(s3Bucket)

    val deletable = with(CacheDB.selectDeletedDatasets()) {
      val threshold = OffsetDateTime.now().minus(config.pruneAge.inWholeSeconds, ChronoUnit.SECONDS)
      filter { it.wasDeletedLongEnoughAgoToPrune(dsm, threshold) }
    }

    log.info("found {} candidates for pruning", deletable.size)

    deletable.forEach {
      try {
        runDelete(dsm, s3Bucket, it)
      } catch (e: Throwable) {
        log.error("failed to delete dataset ${it.ownerID}/${it.datasetID} due to exception:", e)
      }
    }

  }

  private fun DeletedDataset.wasDeletedLongEnoughAgoToPrune(dsm: DatasetManager, threshold: OffsetDateTime): Boolean {
    val dir = dsm.getDatasetDirectory(ownerID, datasetID)

    // If the directory doesn't exist, then it has already been deleted.
    if (!dir.exists()) {
      log.warn("dataset {}/{} still has a record in the cache db even though it has been deleted from S3", ownerID, datasetID)
      return false
    }

    if (!dir.hasDeleteFlag()) {
      log.error("dataset {}/{} is marked as deleted in the cache db but has no delete flag in S3", ownerID, datasetID)
      return false
    }

    return dir.getDeleteFlag()
      .lastModified()!!
      .isBefore(threshold)
  }

  private fun runDelete(dsm: DatasetManager, bucket: S3Bucket, ds: DeletedDataset) {
    log.info("hard-deleting dataset {}/{}", ds.ownerID, ds.datasetID)

    val dir  = dsm.getDatasetDirectory(ds.ownerID, ds.datasetID)
    val meta = dir.getMeta().load()!!

    // Delete records from app databases
    meta.projects.forEach { projectID -> ds.deleteFromAppDB(projectID) }

    // Delete records from cache DB
    ds.deleteFromCacheDB()

    // Delete objects from S3
    ds.deleteFromS3(bucket)
  }

  private fun DeletedDataset.deleteFromAppDB(projectID: ProjectID) {
    log.debug("deleting dataset {} from project {} app DB", datasetID, projectID)

    AppDB.withTransaction(projectID) {
      it.deleteDatasetVisibilities(datasetID)
      it.deleteDatasetProjectLinks(datasetID)
      it.deleteSyncControl(datasetID)
      it.deleteInstallMessages(datasetID)
      it.deleteDataset(datasetID)
    }
  }

  private fun DeletedDataset.deleteFromCacheDB() {
    log.debug("deleting dataset {} from cache DB", datasetID)

    CacheDB.withTransaction {
      it.deleteDatasetFiles(datasetID)
      it.deleteDatasetMetadata(datasetID)
      it.deleteDatasetProjects(datasetID)
      it.deleteDatasetShareOffers(datasetID)
      it.deleteDatasetShareReceipts(datasetID)
      it.deleteImportControl(datasetID)
      it.deleteImportMessages(datasetID)
      it.deleteSyncControl(datasetID)
      it.deleteDataset(datasetID)
    }
  }

  private fun DeletedDataset.deleteFromS3(bucket: S3Bucket) {
    log.debug("deleting dataset {}/{} from S3", ownerID, datasetID)

    bucket.objects.listSubPaths(S3Paths.datasetDir(ownerID, datasetID))
      .contents()
      .forEach {
        log.trace("deleting object {} from S3", it.path)
        it.delete()
      }
  }
}