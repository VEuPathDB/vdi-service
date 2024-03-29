package vdi.lane.meta

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.DatasetMetaFilename
import org.veupathdb.vdi.lib.common.OriginTimestamp
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.common.util.or
import vdi.component.async.WorkerPool
import vdi.component.db.app.AppDB
import vdi.component.db.app.AppDBTransaction
import vdi.component.db.app.UniqueConstraintViolation
import vdi.component.db.app.model.*
import vdi.component.db.app.withTransaction
import vdi.component.db.cache.CacheDBTransaction
import vdi.component.db.cache.model.DatasetImpl
import vdi.component.db.cache.model.DatasetImportStatus
import vdi.component.db.cache.model.DatasetMetaImpl
import vdi.component.db.cache.withTransaction
import vdi.component.kafka.EventMessage
import vdi.component.kafka.EventSource
import vdi.component.kafka.router.KafkaRouter
import vdi.component.metrics.Metrics
import vdi.component.modules.AbstractVDIModule
import vdi.component.plugin.client.response.inm.InstallMetaBadRequestResponse
import vdi.component.plugin.client.response.inm.InstallMetaResponseType
import vdi.component.plugin.client.response.inm.InstallMetaUnexpectedErrorResponse
import vdi.component.plugin.mapping.PluginHandler
import vdi.component.plugin.mapping.PluginHandlers
import vdi.component.s3.DatasetManager
import java.sql.SQLException
import java.time.OffsetDateTime

internal class UpdateMetaTriggerHandlerImpl(private val config: UpdateMetaTriggerHandlerConfig)
  : UpdateMetaTriggerHandler
  , AbstractVDIModule("update-meta-trigger-handler")
{
  private val log = LoggerFactory.getLogger(javaClass)

  private val cacheDB = vdi.component.db.cache.CacheDB()

  private val appDB = AppDB()

  override suspend fun run() {
    val dm = requireDatasetManager(config.s3Config, config.s3Bucket)
    val kc = requireKafkaConsumer(config.kafkaRouterConfig.updateMetaTriggerTopic, config.kafkaConsumerConfig)
    val kr = requireKafkaRouter(config.kafkaRouterConfig)
    val wp = WorkerPool("update-meta-workers", config.workQueueSize.toInt(), config.workerPoolSize.toInt()) {
      Metrics.updateMetaQueueSize.inc(it.toDouble())
    }

    coroutineScope {
      launch(Dispatchers.IO) {
        while (!isShutDown()) {
          // Select meta trigger messages from Kafka
          kc.fetchMessages(config.kafkaRouterConfig.updateMetaTriggerMessageKey)
            // and for each of the trigger messages received
            .forEach {
              log.info("Received install-meta job for dataset {}/{} from source {}", it.userID, it.datasetID, it.eventSource)
              wp.submit { updateMeta(dm, kr, it) }
            }
        }

        wp.stop()
      }

      wp.start()
    }

    kc.close()
    kr.close()
    confirmShutdown()
  }

  private suspend fun updateMeta(dm: DatasetManager, kr: KafkaRouter, msg: EventMessage) {
    try {
      updateMeta(dm, msg.userID, msg.datasetID)
    } finally {
      if (msg.eventSource != EventSource.Reconciler)
        kr.sendReconciliationTrigger(msg.userID, msg.datasetID)
    }
  }

  private suspend fun updateMeta(dm: DatasetManager, userID: UserID, datasetID: DatasetID) {
    log.debug("Looking up dataset directory for dataset {}/{}", userID, datasetID)

    // lookup the dataset directory for the given userID and datasetID
    val dir = dm.getDatasetDirectory(userID, datasetID)

    // If the dataset directory is not usable, bail out.
    //
    // Don't worry about logging here, the `isUsable` method performs logging
    // specific to the reason that the dataset directory is not usable.
    if (!dir.isUsable(userID, datasetID))
      return

    // Load the dataset metadata from S3
    val datasetMeta   = dir.getMetaFile().load()!!
    val metaTimestamp = dir.getMetaFile().lastModified()!!
    log.info("dataset {}/{} meta timestamp is {}", userID, datasetID, metaTimestamp)

    val timer = Metrics.MetaUpdates.duration
      .labels(datasetMeta.type.name, datasetMeta.type.version)
      .startTimer()

    // Attempt to select the dataset details from the cache DB
    val cachedDataset = cacheDB.selectDataset(datasetID)

    // If no dataset record was found in the cache DB then this is (likely) the
    // first event for the dataset coming through the pipeline.
    if (cachedDataset == null) {
      log.debug("dataset details were not found for dataset {}/{}, creating them", userID, datasetID)
      // If they were not found, construct them
      cacheDB.initializeDataset(datasetID, datasetMeta)
    }

    // Else if the dataset is marked as deleted already, then bail here.
    else {
      if (cachedDataset.isDeleted) {
        log.info("refusing to update dataset {}/{} meta due to it being marked as deleted", userID, datasetID)
        return
      }
    }

    cacheDB.withTransaction { db ->
        // 1. Update meta info
        db.updateDatasetMeta(DatasetMetaImpl(
          datasetID   = datasetID,
          visibility  = datasetMeta.visibility,
          name        = datasetMeta.name,
          summary     = datasetMeta.summary,
          description = datasetMeta.description,
          sourceURL   = datasetMeta.sourceURL,
        ))

        // 2. Update meta timestamp
        db.updateMetaSyncControl(datasetID, metaTimestamp)
      }

    if (!PluginHandlers.contains(datasetMeta.type.name, datasetMeta.type.version)) {
      log.error("dataset {}/{} declares a type of {}:{} which is unknown to the vdi service", userID, datasetID, datasetMeta.type.name, datasetMeta.type.version)
      return
    }

    val ph = PluginHandlers[datasetMeta.type.name, datasetMeta.type.version]!!

    datasetMeta.projects
      .forEach { projectID -> updateTargetMeta(ph, datasetMeta, metaTimestamp, datasetID, projectID, userID) }

    timer.observeDuration()
  }

  private suspend fun updateTargetMeta(
    ph: PluginHandler,
    meta: VDIDatasetMeta,
    metaTimestamp: OffsetDateTime,
    datasetID: DatasetID,
    projectID: ProjectID,
    userID: UserID,
  ) {
    if (!ph.appliesToProject(projectID)) {
      log.warn("dataset {}/{} declares a project id of {} which is not applicable to dataset type {}:{}", userID, datasetID, projectID, meta.type.name, meta.type.version)
      return
    }

    val appDb = appDB.accessor(projectID)
    if (appDb == null) {
      log.info("skipping dataset {}/{}, project {} update meta due to target being disabled", userID, datasetID, projectID)
      return
    }

    val failed = appDb
      .selectDatasetInstallMessages(datasetID)
      .any { it.status == InstallStatus.FailedInstallation || it.status == InstallStatus.FailedValidation }

    if (failed) {
      log.info("skipping install-meta for dataset {}/{}, project {} due to previous failures", userID, datasetID, projectID)
      return
    }

    appDB.withTransaction(projectID) {
      try {
        log.debug("testing for existence of dataset {}/{} in app db for project {}", userID, datasetID, projectID)
        it.selectDataset(datasetID) or {
          log.debug("inserting dataset record for dataset {}/{} into app db for project {}", userID, datasetID, projectID)
          it.insertDataset(DatasetRecord(
            datasetID   = datasetID,
            owner       = meta.owner,
            typeName    = meta.type.name,
            typeVersion = meta.type.version,
            isDeleted   = DeleteFlag.NotDeleted,
          ))

          it.insertDatasetVisibility(datasetID, meta.owner)

          log.debug("inserting sync control record for dataset {}/{} into app db for project {}", userID, datasetID, projectID)
          it.insertSyncControl(VDISyncControlRecord(
            datasetID     = datasetID,
            sharesUpdated = OriginTimestamp,
            dataUpdated   = OriginTimestamp,
            metaUpdated   = OriginTimestamp,
          ))

          log.debug("inserting dataset project link for dataset {}/{} into app db for project {}", userID, datasetID, projectID)
          it.insertDatasetProjectLink(datasetID, projectID)
        }

        log.debug("upserting dataset meta record for dataset {}/{} into app db for project {}", userID, datasetID, projectID)
        it.upsertDatasetMeta(datasetID, meta.name, meta.description)

        it.selectDatasetSyncControlRecord(datasetID) or {
          it.insertSyncControl(VDISyncControlRecord(
            datasetID     = datasetID,
            sharesUpdated = OriginTimestamp,
            dataUpdated   = OriginTimestamp,
            metaUpdated   = OriginTimestamp,
          ))
        }
      } catch (e: Throwable) {
        log.error("exception while attempting to getOrCreate app db records for dataset $userID/$datasetID", e)
        it.rollback()
        throw e
      }
    }

    val sync = appDB.accessor(projectID)!!.selectDatasetSyncControlRecord(datasetID)!!

    if (!sync.metaUpdated.isBefore(metaTimestamp)) {
      log.info("skipping install-meta for dataset {}/{}, project {} as nothing has changed.", userID, datasetID, projectID)
      return
    }

    appDB.withTransaction(projectID) {
      log.debug("upserting install-meta message for dataset {}/{} into app db for project {}", userID, datasetID, projectID)
      it.upsertInstallMetaMessage(datasetID, InstallStatus.Running)
    }

    val result = ph.client.postInstallMeta(datasetID, projectID, meta)

    Metrics.MetaUpdates.count.labels(meta.type.name, meta.type.version, result.responseCode.toString()).inc()

    try {
      when (result.type) {
        InstallMetaResponseType.Success -> handleSuccessResponse(userID, datasetID, projectID)
        InstallMetaResponseType.BadRequest -> handleBadRequestResponse(userID, datasetID, projectID, result as InstallMetaBadRequestResponse)
        InstallMetaResponseType.UnexpectedError -> handleUnexpectedErrorResponse(userID, datasetID, projectID, result as InstallMetaUnexpectedErrorResponse)
      }
    } catch (e: Throwable) {
      log.info("install-meta request to handler server failed with exception:", e)
      appDB.withTransaction(projectID) {
        try {
          it.upsertInstallMetaMessage(datasetID, InstallStatus.FailedInstallation)
        } catch (e: SQLException) {
          if (e.errorCode == 1) {
            log.info("unique key constraint violation on dataset {}/{} install meta, assuming race condition.", userID, datasetID)
          } else {
            throw e;
          }
        }
      }
      throw e
    } finally {
      appDB.withTransaction(projectID) { it.updateSyncControlMetaTimestamp(datasetID, metaTimestamp) }
    }
  }

  private fun handleSuccessResponse(userID: UserID, datasetID: DatasetID, projectID: ProjectID) {
    log.info("dataset handler server reports dataset {}/{} meta installed successfully into project {}", userID, datasetID, projectID)
    appDB.withTransaction(projectID) { it.upsertInstallMetaMessage(datasetID, InstallStatus.Complete) }
  }

  private fun AppDBTransaction.upsertInstallMetaMessage(datasetID: DatasetID, status: InstallStatus) {
    val message = DatasetInstallMessage(datasetID, InstallType.Meta, status, null)

    try {
      insertDatasetInstallMessage(message)
    } catch (e: SQLException) {
      if (e.errorCode == UniqueConstraintViolation)
        updateDatasetInstallMessage(message)
      else
        throw e
    }
  }

  private fun handleBadRequestResponse(userID: UserID, datasetID: DatasetID, projectID: ProjectID, res: InstallMetaBadRequestResponse) {
    log.error("dataset handler server reports 400 error for meta-install on dataset {}/{}, project {}", userID, datasetID, projectID)
    throw IllegalStateException(res.message)
  }

  private fun handleUnexpectedErrorResponse(userID: UserID, datasetID: DatasetID, projectID: ProjectID, res: InstallMetaUnexpectedErrorResponse) {
    log.error("dataset handler server reports 500 error for meta-install on dataset {}/{}, project {}", userID, datasetID, projectID)
    throw IllegalStateException(res.message)
  }

  private fun vdi.component.s3.DatasetDirectory.isUsable(userID: UserID, datasetID: DatasetID): Boolean {
    if (!exists()) {
      log.warn("got an update-meta event for dataset {}/{} which has no directory?", userID, datasetID)
      return false
    }

    if (hasDeleteFlag()) {
      log.info("got an update-meta event for dataset {}/{} which has a delete flag, ignoring it.", userID, datasetID)
      return false
    }

    if (!hasMetaFile()) {
      log.warn("got an update-meta event for dataset {}/{} which has no {} file?", userID, datasetID, DatasetMetaFilename)
      return false
    }

    return true
  }

  private fun vdi.component.db.cache.CacheDB.initializeDataset(datasetID: DatasetID, meta: VDIDatasetMeta) {
    openTransaction().use {

      // Insert a new dataset record
      it.tryInsertDataset(DatasetImpl(
        datasetID    = datasetID,
        typeName     = meta.type.name,
        typeVersion  = meta.type.version,
        ownerID      = meta.owner,
        isDeleted    = false,
        origin       = meta.origin,
        created      = meta.created,
        importStatus = DatasetImportStatus.Queued,
        inserted     = OffsetDateTime.now(),
      ))

      // insert metadata for the dataset
      it.tryInsertDatasetMeta(DatasetMetaImpl(
        datasetID   = datasetID,
        visibility  = meta.visibility,
        name        = meta.name,
        summary     = meta.summary,
        description = meta.description,
        sourceURL   = meta.sourceURL,
      ))

      // Insert an import control record for the dataset
      it.tryInsertImportControl(datasetID, DatasetImportStatus.Queued)

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
}
