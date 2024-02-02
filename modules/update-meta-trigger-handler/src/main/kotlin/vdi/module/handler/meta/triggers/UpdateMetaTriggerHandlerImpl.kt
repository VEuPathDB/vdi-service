package vdi.module.handler.meta.triggers

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.DatasetMetaFilename
import org.veupathdb.vdi.lib.common.OriginTimestamp
import org.veupathdb.vdi.lib.common.async.WorkerPool
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.common.util.or
import org.veupathdb.vdi.lib.db.app.AppDB
import org.veupathdb.vdi.lib.db.app.AppDBTransaction
import org.veupathdb.vdi.lib.db.app.model.*
import org.veupathdb.vdi.lib.db.cache.CacheDB
import org.veupathdb.vdi.lib.db.cache.CacheDBTransaction
import org.veupathdb.vdi.lib.db.cache.model.DatasetImpl
import org.veupathdb.vdi.lib.db.cache.model.DatasetImportStatus
import org.veupathdb.vdi.lib.db.cache.model.DatasetMetaImpl
import org.veupathdb.vdi.lib.handler.client.response.inm.InstallMetaBadRequestResponse
import org.veupathdb.vdi.lib.handler.client.response.inm.InstallMetaResponseType
import org.veupathdb.vdi.lib.handler.client.response.inm.InstallMetaUnexpectedErrorResponse
import org.veupathdb.vdi.lib.handler.mapping.PluginHandler
import org.veupathdb.vdi.lib.handler.mapping.PluginHandlers
import org.veupathdb.vdi.lib.kafka.model.triggers.InstallTrigger
import org.veupathdb.vdi.lib.kafka.model.triggers.ShareTrigger
import org.veupathdb.vdi.lib.kafka.model.triggers.UpdateMetaTrigger
import org.veupathdb.vdi.lib.kafka.router.KafkaRouter
import org.veupathdb.vdi.lib.kafka.router.KafkaRouterFactory
import org.veupathdb.vdi.lib.s3.datasets.DatasetDirectory
import org.veupathdb.vdi.lib.s3.datasets.DatasetManager
import vdi.component.metrics.Metrics
import vdi.component.modules.VDIServiceModuleBase
import java.sql.SQLException
import java.time.OffsetDateTime

internal class UpdateMetaTriggerHandlerImpl(private val config: UpdateMetaTriggerHandlerConfig)
  : UpdateMetaTriggerHandler
  , VDIServiceModuleBase("update-meta-trigger-handler")
{
  private val log = LoggerFactory.getLogger(javaClass)

  override suspend fun run() {
    val dm = DatasetManager(requireS3Bucket(requireS3Client(config.s3Config), config.s3Bucket))
    val kc = requireKafkaConsumer(config.kafkaRouterConfig.updateMetaTriggerTopic, config.kafkaConsumerConfig)
    val kr = requireKafkaRouter()
    val wp = WorkerPool("update-meta-workers", config.workQueueSize.toInt(), config.workerPoolSize.toInt()) {
      Metrics.updateMetaQueueSize.inc(it.toDouble())
    }

    runBlocking(Dispatchers.IO) {
      launch {
        while (!isShutDown()) {
          // Select meta trigger messages from Kafka
          kc.fetchMessages(config.kafkaRouterConfig.updateMetaTriggerMessageKey, UpdateMetaTrigger::class)
            // and for each of the trigger messages received
            .forEach { (userID, datasetID) ->
              log.info("Received install-meta job for dataset {}/{}.", userID, datasetID)
              wp.submit { executeJob(dm, kr, userID, datasetID) }
            }
        }

        wp.stop()
      }

      wp.start()
    }

    confirmShutdown()
  }

  private fun executeJob(dm: DatasetManager, kr: KafkaRouter, userID: UserID, datasetID: DatasetID) {
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
    val datasetMeta   = dir.getMeta().load()!!
    val metaTimestamp = dir.getMeta().lastModified()!!
    log.info("Meta timestamp is {}", metaTimestamp)

    val timer = Metrics.metaUpdateTimes
      .labels(datasetMeta.type.name, datasetMeta.type.version)
      .startTimer()

    // Attempt to select the dataset details from the cache DB
    val cachedDataset = CacheDB.selectDataset(datasetID)

    // If no dataset record was found in the cache DB then this is (likely) the
    // first event for the dataset coming through the pipeline.
    if (cachedDataset == null) {
      log.debug("dataset details were not found for dataset {}/{}, creating them", userID, datasetID)
      // If they were not found, construct them
      CacheDB.initializeDataset(datasetID, datasetMeta)
    }

    // Else if the dataset is marked as deleted already, then bail here.
    else {
      if (cachedDataset.isDeleted) {
        log.info("refusing to update dataset {}/{} meta due to it being marked as deleted", userID, datasetID)
        return
      }
    }

    // Attempt to look up the sync control record for the dataset in
    // the cache DB.
    val syncControl = CacheDB.selectSyncControl(datasetID) or {
      // If the sync control record was not found for some reason
      CacheDB.initSyncControl(datasetID)
      CacheDB.selectSyncControl(datasetID)!!
    }

    // Do the "little" reconciliation
    comparison(dir, syncControl, userID, datasetID)
      .also {
        if (it.doDataSync) {
          log.info("doing little reconciliation data sync for dataset {}/{}", userID, datasetID)
          kr.sendInstallTrigger(InstallTrigger(userID, datasetID))
        }

        if (it.doShareSync) {
          log.info("doing little reconciliation share sync for dataset {}/{}", userID, datasetID)
          kr.sendShareTrigger(ShareTrigger(userID, datasetID))
        }
      }

    CacheDB.openTransaction()
      .use { db ->
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
      log.error("dataset {}/{} declares a type of {} which is unknown to the vdi service", userID, datasetID, datasetMeta.type.name)
      return
    }

    val ph = PluginHandlers[datasetMeta.type.name, datasetMeta.type.version]!!

    datasetMeta.projects
      .forEach { projectID -> executeJob(ph, datasetMeta, metaTimestamp, datasetID, projectID, userID) }

    timer.observeDuration()
  }

  private fun executeJob(
    ph: PluginHandler,
    meta: VDIDatasetMeta,
    metaTimestamp: OffsetDateTime,
    datasetID: DatasetID,
    projectID: ProjectID,
    userID: UserID,
  ) {
    if (!ph.appliesToProject(projectID)) {
      log.warn("dataset {}/{} declares a project id of {} which is not applicable to dataset type {}", userID, datasetID, projectID, meta.type.name)
      return
    }

    val appDb = AppDB.accessor(projectID)
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

    AppDB.withTransaction(projectID) {
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

    val sync = AppDB.accessor(projectID)!!.selectDatasetSyncControlRecord(datasetID)!!

    if (!sync.metaUpdated.isBefore(metaTimestamp)) {
      log.info("skipping install-meta for dataset {}/{}, project {} as nothing has changed.", userID, datasetID, projectID)
      return
    }

    val result = ph.client.postInstallMeta(datasetID, projectID, meta)

    Metrics.metaUpdates.labels(meta.type.name, meta.type.version, result.responseCode.toString()).inc()

    try {
      when (result.type) {
        InstallMetaResponseType.Success -> handleSuccessResponse(userID, datasetID, projectID)
        InstallMetaResponseType.BadRequest -> handleBadRequestResponse(userID, datasetID, projectID, result as InstallMetaBadRequestResponse)
        InstallMetaResponseType.UnexpectedError -> handleUnexpectedErrorResponse(userID, datasetID, projectID, result as InstallMetaUnexpectedErrorResponse)
      }
    } catch (e: Throwable) {
      log.info("install-meta request to handler server failed with exception:", e)
      AppDB.withTransaction(projectID) {
        try {
          it.insertDatasetInstallMessage(DatasetInstallMessage(datasetID, InstallType.Meta, InstallStatus.FailedInstallation, e.message))
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
      AppDB.withTransaction(projectID) { it.updateSyncControlMetaTimestamp(datasetID, metaTimestamp) }
    }
  }

  private fun handleSuccessResponse(userID: UserID, datasetID: DatasetID, projectID: ProjectID) {
    log.info("dataset handler server reports dataset {}/{} meta installed successfully into project {}", userID, datasetID, projectID)
    AppDB.withTransaction(projectID) { it.insertMetaInstallSuccessMessage(datasetID) }
  }

  private fun AppDBTransaction.insertMetaInstallSuccessMessage(datasetID: DatasetID) {
    val old = selectDatasetInstallMessage(datasetID, InstallType.Meta)

    // If there is no old record, then attempt to insert one.  There is a race
    // condition here because multiple events may be being processed for the
    // same dataset at the same time.  So we will check for unique constraint
    // violations and attempt an update instead of an insert if one occurs.
    if (old == null) {
      try {
        insertDatasetInstallMessage(DatasetInstallMessage(datasetID, InstallType.Meta, InstallStatus.Complete, null))
      } catch(e: SQLException) {
        // If it was a unique constraint violation then it was a race condition.
        // recall this method to make sure that the states line up
        if (e.errorCode == 1) {
          insertMetaInstallSuccessMessage(datasetID)
        } else {
          throw e;
        }
      }
    } else {
      // If the status that reached the database before us is the same as the
      // status that we are attempting to write, then there is no need to do the
      // update as it will replace the updated timestamp without changing
      // anything which will make things harder to manually trace
      if (old.status != InstallStatus.Complete) {
        updateDatasetInstallMessage(DatasetInstallMessage(datasetID, InstallType.Meta, InstallStatus.Complete, null))
      }
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

  private suspend fun requireKafkaRouter() = safeExec("failed to create KafkaRouter instance") {
    KafkaRouterFactory(config.kafkaRouterConfig).newKafkaRouter()
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

    if (!hasMeta()) {
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

  private fun CacheDB.initSyncControl(datasetID: DatasetID) {
    openTransaction().use { it.initSyncControl(datasetID) }
  }

  private data class SyncActions(
    val doShareSync: Boolean,
    val doDataSync:  Boolean,
  )

  private fun comparison(
    ds: DatasetDirectory,
    cacheDbSyncControl: VDISyncControlRecord,
    userID: UserID,
    datasetID: DatasetID,
  ): SyncActions {
    val dataset = CacheDB.selectDataset(datasetID)!!

    val latestShare = ds.getLatestShareTimestamp(cacheDbSyncControl.sharesUpdated)
    val latestData = ds.getLatestDataTimestamp(cacheDbSyncControl.dataUpdated)

    var doShareSync = cacheDbSyncControl.sharesUpdated.isBefore(latestShare)
    var doDataSync = cacheDbSyncControl.dataUpdated.isBefore(latestData)

    if (doShareSync && doDataSync)
      return SyncActions(doShareSync = true, doDataSync = true)

    for (project in dataset.projects) {
      val appDB = AppDB.accessor(project)
      if (appDB == null) {
        log.info("skipping dataset state comparison for dataset {}/{}, project {} due to the target project config being disabled.", userID, datasetID, project)
        continue
      }

      log.debug("checking project {} for dataset {}/{} to see if it is out of sync", project, userID, datasetID)

      val sync = appDB.selectDatasetSyncControlRecord(datasetID)
        ?: return SyncActions(doShareSync = true, doDataSync = true)

      if (!doShareSync && sync.sharesUpdated.isBefore(latestShare))
        doShareSync = true

      if (!doDataSync && sync.dataUpdated.isBefore(latestData))
        doDataSync = true

      if (doShareSync && doDataSync)
        return SyncActions(doShareSync = true, doDataSync = true)
    }

    return SyncActions(doShareSync, doDataSync)
  }
}
