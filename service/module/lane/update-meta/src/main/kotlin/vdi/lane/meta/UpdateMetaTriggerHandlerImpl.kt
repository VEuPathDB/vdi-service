package vdi.lane.meta

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.veupathdb.vdi.lib.common.DatasetMetaFilename
import org.veupathdb.vdi.lib.common.OriginTimestamp
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility
import org.veupathdb.vdi.lib.common.util.or
import java.sql.SQLException
import java.time.OffsetDateTime
import java.util.concurrent.ConcurrentHashMap
import vdi.lib.async.WorkerPool
import vdi.lib.db.app.AppDB
import vdi.lib.db.app.AppDBTransaction
import vdi.lib.db.app.isUniqueConstraintViolation
import vdi.lib.db.app.model.*
import vdi.lib.db.app.withTransaction
import vdi.lib.db.cache.CacheDB
import vdi.lib.db.cache.CacheDBTransaction
import vdi.lib.db.cache.model.DatasetImpl
import vdi.lib.db.cache.model.DatasetImportStatus
import vdi.lib.db.cache.withTransaction
import vdi.lib.db.model.SyncControlRecord
import vdi.lib.kafka.EventMessage
import vdi.lib.kafka.EventSource
import vdi.lib.kafka.router.KafkaRouter
import vdi.lib.logging.logger
import vdi.lib.metrics.Metrics
import vdi.lib.modules.AbortCB
import vdi.lib.modules.AbstractVDIModule
import vdi.lib.plugin.client.PluginException
import vdi.lib.plugin.client.PluginRequestException
import vdi.lib.plugin.client.response.inm.InstallMetaResponseType
import vdi.lib.plugin.client.response.inm.InstallMetaUnexpectedErrorResponse
import vdi.lib.plugin.mapping.PluginHandler
import vdi.lib.plugin.mapping.PluginHandlers
import vdi.lib.s3.DatasetDirectory
import vdi.lib.s3.DatasetObjectStore

internal class UpdateMetaTriggerHandlerImpl(
  private val config: UpdateMetaTriggerHandlerConfig,
  abortCB: AbortCB,
)
  : UpdateMetaTriggerHandler
  , AbstractVDIModule("update-meta-trigger-handler", abortCB, logger<UpdateMetaTriggerHandler>())
{
  private val datasetsInProgress = ConcurrentHashMap.newKeySet<DatasetID>(32)

  private val cacheDB = runBlocking { safeExec("failed to init Cache DB", ::CacheDB) }

  private val appDB = AppDB()

  override suspend fun run() {
    val dm = requireDatasetManager(config.s3Config, config.s3Bucket)
    val kc = requireKafkaConsumer(config.eventTopic, config.kafkaConsumerConfig)
    val kr = requireKafkaRouter(config.kafkaRouterConfig)
    val wp = WorkerPool("update-meta-workers", config.jobQueueSize, config.workerCount) {
      Metrics.updateMetaQueueSize.inc(it.toDouble())
    }

    coroutineScope {
      launch(Dispatchers.IO) {
        while (!isShutDown()) {
          kc.fetchMessages(config.eventKey)
            .forEach {
              log.info("Received install-meta job for dataset {}/{} from source {}", it.userID, it.datasetID, it.eventSource)
              wp.submit { updateMetaIfNotInProgress(dm, kr, it) }
            }
        }
      }
    }

    wp.stop()
    kc.close()
    kr.close()
    confirmShutdown()
  }

  private suspend fun updateMetaIfNotInProgress(dm: DatasetObjectStore, kr: KafkaRouter, msg: EventMessage) {
    if (datasetsInProgress.add(msg.datasetID)) {
      try {
        tryUpdateMeta(dm, kr, msg)
      } finally {
        datasetsInProgress.remove(msg.datasetID)
      }
    } else {
      log.info("meta update already in progress for dataset {}/{}, ignoring meta event", msg.userID, msg.datasetID)
    }
  }

  private suspend fun tryUpdateMeta(dm: DatasetObjectStore, kr: KafkaRouter, msg: EventMessage) {
    try {
      if (msg.eventSource == EventSource.UpdateMetaLane) {
        log.warn("attempted to recurse update meta on dataset ${msg.userID}/${msg.datasetID}")
      } else {
        updateMeta(dm, msg.userID, msg.datasetID)
        kr.sendReconciliationTrigger(msg.userID, msg.datasetID, EventSource.UpdateMetaLane)
      }
    } catch (e: PluginException) {
      e.log(log::error)
    } catch (e: Throwable) {
      PluginException.installMeta("N/A", "N/A", msg.userID, msg.datasetID, cause = e).log(log::error)
    }
  }

  private suspend fun updateMeta(dm: DatasetObjectStore, userID: UserID, datasetID: DatasetID) {
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
      .labels(datasetMeta.type.name.toString(), datasetMeta.type.version)
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
        db.updateDatasetMeta(datasetID, datasetMeta)

        // 2. Update meta timestamp
        db.updateMetaSyncControl(datasetID, metaTimestamp)
      }

    if (!PluginHandlers.contains(datasetMeta.type.name, datasetMeta.type.version)) {
      log.error("dataset {}/{} declares a type of {}:{} which is unknown to the vdi service", userID, datasetID, datasetMeta.type.name, datasetMeta.type.version)
      return
    }

    val ph = PluginHandlers[datasetMeta.type.name, datasetMeta.type.version]!!

    datasetMeta.projects
      .forEach { projectID -> tryUpdateTargetMeta(ph, datasetMeta, metaTimestamp, datasetID, projectID, userID) }

    timer.observeDuration()
  }

  private suspend fun tryUpdateTargetMeta(
    ph: PluginHandler,
    meta: VDIDatasetMeta,
    metaTimestamp: OffsetDateTime,
    datasetID: DatasetID,
    projectID: ProjectID,
    userID: UserID,
  ) {
    try {
      updateTargetMeta(ph, meta, metaTimestamp, datasetID, projectID, userID)
    } catch (e: PluginException) {
      throw e
    } catch (e: Throwable) {
      throw PluginException.installMeta(ph.displayName, projectID, userID, datasetID, cause = e)
    }
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

    val appDb = appDB.accessor(projectID, ph.type) or {
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

    appDB.withTransaction(projectID, ph.type) {
      try {
        val record = it.getOrInsertDatasetRecord(userID, datasetID, projectID, meta)

        // If the record in the app db has an isPublic flag value different from
        // what we expect, update the app db record.
        if (record.isPublic != (meta.visibility == VDIDatasetVisibility.Public)) {
          it.updateDataset(record.copy(isPublic = meta.visibility == VDIDatasetVisibility.Public))
        }

        log.debug("upserting dataset meta record for dataset {}/{} into app db for project {}", userID, datasetID, projectID)
        it.upsertDatasetMeta(datasetID, meta)

        it.deleteDatasetContacts(datasetID)
        if (meta.contacts.isNotEmpty())
          it.insertDatasetContacts(datasetID, meta.contacts)

        it.deleteDatasetHyperlinks(datasetID)
        if (meta.hyperlinks.isNotEmpty())
          it.insertDatasetHyperlinks(datasetID, meta.hyperlinks)

        it.deleteDatasetPublications(datasetID)
        if (meta.publications.isNotEmpty())
          it.insertDatasetPublications(datasetID, meta.publications)

        it.deleteDatasetOrganisms(datasetID)
        if (meta.organisms.isNotEmpty())
          it.insertDatasetOrganisms(datasetID, meta.organisms)

        it.deleteDatasetProperties(datasetID)
        if (meta.properties != null)
          it.insertDatasetProperties(datasetID, meta.properties!!)

        it.selectDatasetSyncControlRecord(datasetID) or {
          it.insertDatasetSyncControl(SyncControlRecord(
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

    val sync = appDB.accessor(projectID, ph.type)!!.selectDatasetSyncControlRecord(datasetID)!!

    if (!sync.metaUpdated.isBefore(metaTimestamp)) {
      log.info("skipping install-meta for dataset {}/{}, project {} as nothing has changed.", userID, datasetID, projectID)
      return
    }

    appDB.withTransaction(projectID, ph.type) {
      log.debug("upserting install-meta message for dataset {}/{} into app db for project {}", userID, datasetID, projectID)
      it.upsertInstallMetaMessage(datasetID, InstallStatus.Running)
    }

    val result = try {
      ph.client.postInstallMeta(datasetID, projectID, meta)
    } catch (e: Throwable){
      throw PluginRequestException.installMeta(ph.displayName, projectID, userID, datasetID, cause = e)
    }

    Metrics.MetaUpdates.count.labels(meta.type.name.toString(), meta.type.version, result.responseCode.toString()).inc()

    try {
      when (result.type) {
        InstallMetaResponseType.Success -> handleSuccessResponse(ph, userID, datasetID, projectID)

        InstallMetaResponseType.BadRequest -> handleBadRequestResponse(
          ph,
          userID,
          datasetID,
          projectID,
          result as vdi.lib.plugin.client.response.inm.InstallMetaBadRequestResponse,
        )

        InstallMetaResponseType.UnexpectedError -> handleUnexpectedErrorResponse(
          ph,
          userID,
          datasetID,
          projectID,
          result as InstallMetaUnexpectedErrorResponse,
        )
      }
    } catch (e: Throwable) {
      log.info("install-meta request to handler server failed with exception:", e)
      appDB.withTransaction(projectID, ph.type) {
        try {
          it.upsertInstallMetaMessage(datasetID, InstallStatus.FailedInstallation)
        } catch (e: SQLException) {
          if (e.errorCode == 1) {
            log.info("unique key constraint violation on dataset {}/{} install meta, assuming race condition.", userID, datasetID)
          } else {
            throw e
          }
        }
      }
      throw e
    } finally {
      appDB.withTransaction(projectID, ph.type) { it.updateSyncControlMetaTimestamp(datasetID, metaTimestamp) }
    }
  }

  private fun handleSuccessResponse(handler: PluginHandler, userID: UserID, datasetID: DatasetID, projectID: ProjectID) {
    log.info(
      "dataset handler server reports dataset {}/{} meta installed successfully into project {} via plugin {}",
      userID,
      datasetID,
      projectID,
      handler,
    )

    appDB.withTransaction(projectID, handler.type) { it.upsertInstallMetaMessage(datasetID, InstallStatus.Complete) }
  }

  private fun AppDBTransaction.upsertInstallMetaMessage(datasetID: DatasetID, status: InstallStatus) {
    val message = DatasetInstallMessage(datasetID, InstallType.Meta, status, null)
    upsertDatasetInstallMessage(message)
  }

  private fun AppDBTransaction.getOrInsertDatasetRecord(
    userID: UserID,
    datasetID: DatasetID,
    projectID: ProjectID,
    meta: VDIDatasetMeta,
  ): DatasetRecord {
    // Return the dataset if found
    selectDataset(datasetID)?.also { return it }

    log.debug("inserting dataset record for dataset {}/{} into app db for project {}", userID, datasetID, projectID)

    val record = DatasetRecord(
      datasetID       = datasetID,
      owner           = meta.owner,
      typeName        = meta.type.name,
      typeVersion     = meta.type.version,
      deletionState   = DeleteFlag.NotDeleted,
      isPublic        = meta.visibility == VDIDatasetVisibility.Public,
      accessibility   = meta.visibility,
      daysForApproval = -1,
      creationDate    = meta.created,
    )

    // this stuff is not project specific!
    //
    // NOTE: We don't install meta stuff here because we will be synchronizing
    // it immediately after this function call anyway.
    try {
      insertDataset(record)
      insertDatasetVisibility(datasetID, meta.owner)
      insertDatasetDependencies(datasetID, meta.dependencies)

      upsertInstallMetaMessage(datasetID, InstallStatus.Running)

      log.debug("inserting sync control record for dataset {}/{} into app db for project {}", userID, datasetID, projectID)
      insertDatasetSyncControl(SyncControlRecord(
        datasetID     = datasetID,
        sharesUpdated = OriginTimestamp,
        dataUpdated   = OriginTimestamp,
        metaUpdated   = OriginTimestamp,
      ))
    } catch (e: SQLException) {
      // Race condition: someone else inserted the record before us, which
      // should be fine.
      if (!isUniqueConstraintViolation(e))
        throw e
    }

    try {
      log.debug("inserting dataset project link for dataset {}/{} into app db for project {}", userID, datasetID, projectID)
      insertDatasetProjectLink(datasetID, projectID)
    } catch (e: SQLException) {
      if (!isUniqueConstraintViolation(e))
        throw e
    }

    return record
  }

  private fun handleBadRequestResponse(
    handler: PluginHandler,
    userID: UserID,
    datasetID: DatasetID,
    projectID: ProjectID,
    res: vdi.lib.plugin.client.response.inm.InstallMetaBadRequestResponse
  ) {
    log.error(
      "dataset handler server reports 400 error for meta-install on dataset {}/{}, project {} via plugin {}",
      userID,
      datasetID,
      projectID,
      handler.displayName,
    )

    throw PluginException.installMeta(handler.displayName, projectID, userID, datasetID, res.message)
  }

  private fun handleUnexpectedErrorResponse(
    handler: PluginHandler,
    userID: UserID,
    datasetID: DatasetID,
    projectID: ProjectID,
    res: InstallMetaUnexpectedErrorResponse,
  ) {
    log.error(
      "dataset handler server reports 500 error for meta-install on dataset {}/{}, project {} via plugin {}",
      userID,
      datasetID,
      projectID,
      handler.displayName
    )

    throw PluginException.installMeta(handler.displayName, projectID, userID, datasetID, res.message)
  }

  private fun DatasetDirectory.isUsable(userID: UserID, datasetID: DatasetID): Boolean {
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

  private fun CacheDB.initializeDataset(datasetID: DatasetID, meta: VDIDatasetMeta) {
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
      it.tryInsertDatasetMeta(datasetID, meta)

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
    tryInsertSyncControl(SyncControlRecord(
      datasetID     = datasetID,
      sharesUpdated = OriginTimestamp,
      dataUpdated   = OriginTimestamp,
      metaUpdated   = OriginTimestamp
    ))
  }
}
