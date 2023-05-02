package vdi.module.handler.meta.triggers

import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.LoggerFactory
import org.veupathdb.lib.s3.s34k.S3Api
import org.veupathdb.lib.s3.s34k.S3Client
import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.lib.s3.s34k.fields.BucketName
import org.veupathdb.vdi.lib.common.OriginTimestamp
import org.veupathdb.vdi.lib.common.async.ShutdownSignal
import org.veupathdb.vdi.lib.common.async.WorkerPool
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.common.util.or
import org.veupathdb.vdi.lib.db.app.AppDB
import org.veupathdb.vdi.lib.db.app.model.DatasetInstallMessage
import org.veupathdb.vdi.lib.db.app.model.DatasetRecord
import org.veupathdb.vdi.lib.db.app.model.InstallStatus
import org.veupathdb.vdi.lib.db.app.model.InstallType
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.db.cache.CacheDBTransaction
import org.veupathdb.vdi.lib.db.cache.model.DatasetImpl
import org.veupathdb.vdi.lib.db.cache.model.DatasetImportStatus
import org.veupathdb.vdi.lib.db.cache.model.DatasetMetaImpl
import org.veupathdb.vdi.lib.handler.client.response.inm.*
import org.veupathdb.vdi.lib.handler.mapping.PluginHandlers
import org.veupathdb.vdi.lib.json.JSON
import org.veupathdb.vdi.lib.kafka.KafkaConsumer
import org.veupathdb.vdi.lib.kafka.model.triggers.InstallTrigger
import org.veupathdb.vdi.lib.kafka.model.triggers.ShareTrigger
import org.veupathdb.vdi.lib.kafka.model.triggers.UpdateMetaTrigger
import org.veupathdb.vdi.lib.kafka.router.KafkaRouter
import org.veupathdb.vdi.lib.kafka.router.KafkaRouterFactory
import org.veupathdb.vdi.lib.s3.datasets.DatasetDirectory
import org.veupathdb.vdi.lib.s3.datasets.DatasetManager
import java.time.OffsetDateTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import vdi.module.handler.meta.triggers.config.UpdateMetaTriggerHandlerConfig

internal class UpdateMetaTriggerHandlerImpl(
  private val config: UpdateMetaTriggerHandlerConfig
) : UpdateMetaTriggerHandler {

  private val log = LoggerFactory.getLogger(javaClass)

  @Volatile
  private var started = false

  private val shutdownTrigger = ShutdownSignal()
  private val shutdownConfirm = ShutdownSignal()

  override suspend fun start() {
    if (!started) {
      log.info("starting update-meta-trigger-handler module")

      started = true
      run()
    }
  }

  override suspend fun stop() {
    log.info("triggering update-meta-trigger-handler shutdown")
    shutdownTrigger.trigger()
    shutdownConfirm.await()
  }

  private suspend fun run() {
    val dm = DatasetManager(S3Api.requireClient().requireBucket(config.s3Bucket))
    val kc = requireKafkaConsumer()
    val kr = requireKafkaRouter()
    val wp = WorkerPool("update-meta-workers", config.workerPoolSize.toInt(), config.workerPoolSize.toInt())

    runBlocking(Dispatchers.IO) {
      launch {
        while (!shutdownTrigger.isTriggered()) {
          // Select meta trigger messages from Kafka
          kc.selectMetaTriggers()
            // and for each of the trigger messages received
            .forEach { (userID, datasetID) -> wp.submit { executeJob(dm, kr, userID, datasetID) } }
        }

        wp.stop()
      }

      wp.start()
    }

    shutdownConfirm.trigger()
  }

  private fun executeJob(dm: DatasetManager, kr: KafkaRouter, userID: UserID, datasetID: DatasetID) {
    log.trace("executeJob(dm=..., kr=..., userID={}, datasetID={})", userID, datasetID)

    log.debug("looking up dataset directory for user {}, dataset {}", userID, datasetID)
    // lookup the dataset directory for the given userID and datasetID
    val dir = dm.getDatasetDirectory(userID, datasetID)

    // If the dataset directory is not usable, bail out.
    //
    // Don't worry about logging here, the `isUsable` method performs
    // logging specific to the reason that the dataset directory is
    // not usable.
    if (!dir.isUsable(userID, datasetID))
      return

    // Load the dataset metadata from S3
    val datasetMeta   = dir.getMeta().load()!!
    val metaTimestamp = dir.getMeta().lastModified()!!

    // Attempt to select the dataset details from the cache DB
    CacheDB.selectDataset(datasetID) or {
      log.debug("dataset details were not found for dataset {}, creating them", datasetID)
      // If they were not found, construct them
      CacheDB.initializeDataset(datasetID, datasetMeta)
    }

    // Attempt to look up the sync control record for the dataset in
    // the cache DB.
    val syncControl = CacheDB.selectSyncControl(datasetID) or {
      // If the sync control record was not found for some reason
      CacheDB.initSyncControl(datasetID)
      CacheDB.selectSyncControl(datasetID)!!
    }

    // Do a "little" reconciliation
    comparison(dir, syncControl)
      .also {
        if (it.doDataSync)
          kr.sendInstallTrigger(InstallTrigger(userID, datasetID))

        if (it.doShareSync)
          kr.sendShareTrigger(ShareTrigger(userID, datasetID))
      }

    CacheDB.openTransaction()
      .use { db ->
        // 1. Update meta info
        db.updateDatasetMeta(DatasetMetaImpl(
          datasetID   = datasetID,
          name        = datasetMeta.name,
          summary     = datasetMeta.summary,
          description = datasetMeta.description,
        ))

        // 2. Update meta timestamp
        db.updateMetaSyncControl(datasetID, metaTimestamp)
      }

    if (datasetMeta.type.name !in PluginHandlers) {
      log.error("dataset {} declares a type of {} which is unknown to the vdi service", datasetID, datasetMeta.type.name)
      return
    }

    val ph = PluginHandlers[datasetMeta.type.name]!!

    datasetMeta.projects
      .forEach projects@{ projectID ->
        if (!ph.appliesToProject(projectID)) {
          log.warn("dataset {} declares a project id of {} which is not applicable to dataset type {}", datasetID, projectID, datasetMeta.type.name)
          return@projects
        }

        // TODO: Verify there isn't a failed install record here already!!!
        val failed = AppDB.accessor(projectID)
          .selectDatasetInstallMessages(datasetID)
          .any { it.status == InstallStatus.FailedInstallation || it.status == InstallStatus.FailedValidation }

        if (failed) {
          log.debug("dataset {}, project {} install-meta is being skipped due to previous failures", datasetID, projectID)
          return@projects
        }

        AppDB.withTransaction(projectID) {
          try {
            log.debug("testing for existence of dataset {} in app db for project {}", datasetID, projectID)
            it.selectDataset(datasetID) or {
              log.debug("inserting dataset record for dataset {} into app db for project {}", datasetID, projectID)
              it.insertDataset(DatasetRecord(
                datasetID   = datasetID,
                owner       = datasetMeta.owner,
                typeName    = datasetMeta.type.name,
                typeVersion = datasetMeta.type.version,
                isDeleted   = false
              ))

              log.debug("inserting sync control record for dataset {} into app db for project {}", datasetID, projectID)
              it.insertSyncControl(VDISyncControlRecord(
                datasetID     = datasetID,
                sharesUpdated = syncControl.sharesUpdated,
                dataUpdated   = syncControl.dataUpdated,
                metaUpdated   = metaTimestamp
              ))

              log.debug("inserting dataset project link for dataset {} into app db for project {}", datasetID, projectID)
              it.insertDatasetProjectLink(datasetID, projectID)
            }

            it.selectDatasetSyncControlRecord(datasetID) or {
              it.insertSyncControl(VDISyncControlRecord(
                datasetID     = datasetID,
                sharesUpdated = syncControl.sharesUpdated,
                dataUpdated   = syncControl.dataUpdated,
                metaUpdated   = metaTimestamp
              ))
            }
          } catch (e: Throwable) {
            log.error("exception while attempting to getOrCreate app db records for dataset $datasetID", e)
            it.rollback()
            throw e
          }
        }

        val result = ph.client.postInstallMeta(datasetID, projectID, datasetMeta)

        try {
          when (result) {
            is InstallMetaSuccessResponse         -> handleSuccessResponse(datasetID, projectID)
            is InstallMetaBadRequestResponse      -> handleBadRequestResponse(datasetID, projectID, result)
            is InstallMetaUnexpectedErrorResponse -> handleUnexpectedErrorResponse(datasetID, projectID, result)
            else                                  -> handleImpossibleCase()
          }
        } catch (e: Throwable) {
          log.debug("install-meta request to handler server failed with exception:", e)
          AppDB.withTransaction(projectID) {
            it.insertDatasetInstallMessage(DatasetInstallMessage(datasetID, InstallType.Meta, InstallStatus.FailedInstallation, e.message))
          }
          throw e
        }
      }
  }

  private fun handleSuccessResponse(datasetID: DatasetID, projectID: ProjectID) {
    log.info("dataset handler server reports dataset {} meta installed successfully into project {}", datasetID, projectID)

    AppDB.withTransaction(projectID) {
      it.insertDatasetInstallMessage(DatasetInstallMessage(datasetID, InstallType.Meta, InstallStatus.Complete, null))
    }
  }

  private fun handleBadRequestResponse(datasetID: DatasetID, projectID: ProjectID, res: InstallMetaBadRequestResponse) {
    log.error("dataset handler server reports 400 error for meta-install on dataset {}, project {}", datasetID, projectID)
    throw IllegalStateException(res.message)
  }

  private fun handleUnexpectedErrorResponse(datasetID: DatasetID, projectID: ProjectID, res: InstallMetaUnexpectedErrorResponse) {
    log.error("dataset handler server reports 500 error for meta-install on dataset {}, project {}", datasetID, projectID)
    throw IllegalStateException(res.message)
  }

  private fun handleImpossibleCase()  {
    log.error("developer error of some sort, we hit the impossible case, did we add a new response type to the handler client?")
    throw IllegalStateException("impossible case hit, did we add a new response type to the handler client?")
  }


  private suspend inline fun <T> safeExec(err: String, fn: () -> T): T =
    try {
      fn()
    } catch (e: Throwable) {
      shutdownTrigger.trigger()
      shutdownConfirm.trigger()
      log.error(err, e)
      throw e
    }

  private suspend fun S3Api.requireClient() = safeExec("failed to create S3 client instance") {
    newClient(config.s3Config)
  }

  private suspend fun S3Client.requireBucket(name: BucketName): S3Bucket {
    val bucket = buckets[name]

    if (bucket == null) {
      shutdownTrigger.trigger()
      shutdownConfirm.trigger()
      log.error("s3 bucket {} does not appear to exist", config.s3Bucket)
      throw IllegalStateException("s3 bucket ${config.s3Bucket} does not appear to exist")
    }

    return bucket
  }

  private suspend fun requireKafkaConsumer() = safeExec("failed to create KafkaConsumer instance") {
    KafkaConsumer(config.kafkaRouterConfig.updateMetaTriggerTopic, config.kafkaConsumerConfig)
  }

  private suspend fun requireKafkaRouter() = safeExec("failed to create KafkaRouter instance") {
    KafkaRouterFactory(config.kafkaRouterConfig).newKafkaRouter()
  }

  private fun KafkaConsumer.selectMetaTriggers() =
    receive()
      .asSequence()
      .filter { it.key == config.kafkaRouterConfig.updateMetaTriggerMessageKey }
      .map {
        try {
          JSON.readValue<UpdateMetaTrigger>(it.value)
        } catch (e: Throwable) {
          log.warn("received an invalid message body from Kafka: {}", it)
          null
        }
      }
      .filterNotNull()

  private fun DatasetDirectory.isUsable(userID: UserID, datasetID: DatasetID): Boolean {
    if (!exists()) {
      log.warn("got an update-meta event for a dataset directory that does not exist?  Dataset: {}, User: {}", datasetID, userID)
      return false
    }

    if (hasDeleteFlag()) {
      log.info("got an update-meta event for a dataset with a delete flag, ignoring it.  Dataset: {}, User: {}", datasetID, userID)
      return false
    }

    if (!hasMeta()) {
      log.warn("got an update-meta event for a dataset that has no meta.json file?  Dataset: {}, User: {}", datasetID, userID)
      return false
    }

    return true
  }

  private fun CacheDB.initializeDataset(datasetID: DatasetID, meta: VDIDatasetMeta) {
    openTransaction().use {

      // Insert a new dataset record
      it.tryInsertDataset(DatasetImpl(
        datasetID   = datasetID,
        typeName    = meta.type.name,
        typeVersion = meta.type.version,
        ownerID     = meta.owner,
        isDeleted   = false,
        created     = OffsetDateTime.now(),
        DatasetImportStatus.AwaitingImport
      ))

      // insert metadata for the dataset
      it.tryInsertDatasetMeta(DatasetMetaImpl(
        datasetID   = datasetID,
        name        = meta.name,
        summary     = meta.summary,
        description = meta.description,
      ))

      // Insert an import control record for the dataset
      it.tryInsertImportControl(datasetID, DatasetImportStatus.AwaitingImport)

      // insert project links for the dataset
      it.tryInsertDatasetProjects(datasetID, meta.projects)

      // insert a sync control record for the dataset using an old timestamp
      // that will predate any possible upload timestamp.
      it.initSyncControl(datasetID)
    }
  }

  private fun CacheDBTransaction.initSyncControl(datasetID: DatasetID) {
    tryInsertSyncControl(
      VDISyncControlRecord(
        datasetID     = datasetID,
        sharesUpdated = OriginTimestamp,
        dataUpdated   = OriginTimestamp,
        metaUpdated   = OriginTimestamp
      )
    )
  }

  private fun CacheDB.initSyncControl(datasetID: DatasetID) {
    openTransaction().use { it.initSyncControl(datasetID) }
  }

  private data class SyncActions(
    val doShareSync: Boolean,
    val doDataSync:  Boolean,
  )

  private fun comparison(ds: DatasetDirectory, lu: VDISyncControlRecord): SyncActions {
    return SyncActions(
      doShareSync = lu.sharesUpdated.isBefore(ds.getLatestShareTimestamp(lu.sharesUpdated)),
      doDataSync  = lu.dataUpdated.isBefore(ds.getLatestDataTimestamp(lu.dataUpdated)),
    )
  }
}
