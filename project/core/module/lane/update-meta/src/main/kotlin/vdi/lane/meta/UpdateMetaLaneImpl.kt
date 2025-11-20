package vdi.lane.meta

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.sql.SQLException
import java.time.OffsetDateTime
import java.util.concurrent.ConcurrentHashMap
import vdi.core.async.WorkerPool
import vdi.core.db.app.AppDB
import vdi.core.db.app.AppDBTransaction
import vdi.core.db.app.isUniqueConstraintViolation
import vdi.core.db.app.model.DatasetInstallMessage
import vdi.core.db.app.model.DatasetRecord
import vdi.core.db.app.model.InstallStatus
import vdi.core.db.app.model.InstallType
import vdi.core.db.app.withTransaction
import vdi.core.db.cache.CacheDB
import vdi.core.db.cache.CacheDBTransaction
import vdi.core.db.cache.model.DatasetImpl
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.core.db.cache.withTransaction
import vdi.core.db.model.SyncControlRecord
import vdi.core.kafka.EventSource
import vdi.core.metrics.Metrics
import vdi.core.modules.AbortCB
import vdi.core.modules.AbstractVDIModule
import vdi.core.plugin.client.PluginException
import vdi.core.plugin.client.PluginRequestException
import vdi.core.plugin.client.response.EmptySuccessResponse
import vdi.core.plugin.client.response.ServiceErrorResponse
import vdi.core.plugin.client.response.ValidationErrorResponse
import vdi.core.plugin.mapping.PluginHandlers
import vdi.core.plugin.registry.PluginRegistry
import vdi.core.s3.DatasetDirectory
import vdi.core.s3.getLatestMappingTimestamp
import vdi.core.util.orElse
import vdi.logging.logger
import vdi.model.DatasetMetaFilename
import vdi.model.OriginTimestamp
import vdi.model.meta.*

internal class UpdateMetaLaneImpl(private val config: UpdateMetaLaneConfig, abortCB: AbortCB)
  : UpdateMetaLane
  , AbstractVDIModule(abortCB, logger<UpdateMetaLane>())
{
  private val datasetsInProgress = ConcurrentHashMap.newKeySet<DatasetID>(32)

  private val cacheDB = runBlocking { safeExec("failed to init Cache DB", ::CacheDB) }

  private val appDB = AppDB()

  override suspend fun run() {
    val dm = requireDatasetManager(config.s3Config, config.s3Bucket)
    val kc = requireKafkaConsumer(config.eventTopic, config.kafkaConsumerConfig)
    val kr = requireKafkaRouter(config.kafkaRouterConfig)
    val wp = WorkerPool.create<UpdateMetaLane>(config.jobQueueSize, config.workerCount) {
      Metrics.updateMetaQueueSize.inc(it.toDouble())
    }

    coroutineScope {
      launch(Dispatchers.IO) {
        while (!isShutDown())
          kc.fetchMessages(config.eventKey)
            .map { UpdateMetaContext(it, dm, kr, logger) }
            .onEach { it.logger.info("received install-meta job from source {}", it.source) }
            .forEach { wp.submit { it.updateMetaIfNotInProgress() } }
      }
    }

    wp.stop()
    kc.close()
    kr.close()
    confirmShutdown()
  }

  private suspend fun UpdateMetaContext.updateMetaIfNotInProgress() {
    if (datasetsInProgress.add(datasetID)) {
      try {
        tryUpdateMeta()
      } finally {
        datasetsInProgress.remove(datasetID)
      }
    } else {
      logger.info("meta update already in progress; ignoring meta event")
    }
  }

  private suspend fun UpdateMetaContext.tryUpdateMeta() {
    try {
      if (source == EventSource.UpdateMetaLane) {
        logger.warn("attempted to recurse update meta")
      } else {
        updateMeta()
        events.sendReconciliationTrigger(ownerID, datasetID, EventSource.UpdateMetaLane)
      }
    } catch (e: PluginException) {
      e.log(logger::error)
    } catch (e: Throwable) {
      PluginException.installMeta("N/A", "N/A", ownerID, datasetID, cause = e).log(logger::error)
    }
  }

  private suspend fun UpdateMetaContext.updateMeta() {
    logger.debug("looking up dataset directory")

    // lookup the dataset directory for the given userID and datasetID
    val dir = store.getDatasetDirectory(ownerID, datasetID)

    // If the dataset directory is not usable, bail out.
    //
    // Don't worry about logging here, the `isUsable` method performs logging
    // specific to the reason that the dataset directory is not usable.
    if (!dir.isUsable())
      return

    // Load the dataset metadata from S3
    val datasetMeta = dir.getMetaFile().load()!!
    val metaTimestamp = dir.getMetaFile().lastModified()!!
    logger.info("dataset meta timestamp is {}", metaTimestamp)

    val mappingTimestamp = dir.getLatestMappingTimestamp() ?: OriginTimestamp
    logger.info("dataset mapping timestamp is {}", mappingTimestamp)

    val timer = Metrics.MetaUpdates.duration
      .labels(datasetMeta.type.name.toString(), datasetMeta.type.version)
      .startTimer()

    // Attempt to select the dataset details from the cache DB
    val cachedDataset = cacheDB.selectDataset(datasetID)

    // If no dataset record was found in the cache DB then this is (likely) the
    // first event for the dataset coming through the pipeline.
    if (cachedDataset == null) {
      logger.debug("dataset details were not found for dataset, creating them")
      // If they were not found, construct them
      cacheDB.initializeDataset(datasetMeta)
    }

    // Else if the dataset is marked as deleted already, then bail here.
    else {
      if (cachedDataset.isDeleted) {
        logger.info("refusing to update dataset meta; dataset marked as deleted")
        return
      }
    }

    cacheDB.withTransaction { db ->
      // 1. Update meta info
      db.updateDatasetMeta(datasetID, datasetMeta)

      // 2. Sync Publications
      db.deletePublications(datasetID)
      db.tryInsertPublications(datasetID, datasetMeta.publications)

      // 3. Update meta timestamp
      db.updateMetaSyncControl(datasetID, metaTimestamp)
    }

    val ph = PluginHandlers[datasetMeta.type] orElse {
      logger.error("dataset declares a type of {} which is unknown to vdi", datasetMeta.type)
      return
    }

    datasetMeta.installTargets
      .forEach { withPlugin(datasetMeta, ph, it).tryUpdateTargetMeta(metaTimestamp) }

    timer.observeDuration()
  }

  private suspend fun UpdateMetaContext.WithPlugin.tryUpdateTargetMeta(metaTimestamp: OffsetDateTime) {
    try {
      updateTargetMeta(metaTimestamp)
    } catch (e: PluginException) {
      throw e
    } catch (e: Throwable) {
      throw PluginException.installMeta(plugin.name, target, ownerID, datasetID, cause = e)
    }
  }

  private suspend fun UpdateMetaContext.WithPlugin.updateTargetMeta(metaTimestamp: OffsetDateTime) {
    if (!plugin.appliesToProject(target)) {
      logger.warn("install target does not support data type; skipping meta update")
      return
    }

    val appDb = appDB.accessor(target, plugin.type) orElse {
      logger.info("skipping meta update; target is disabled")
      return
    }

    val failed = appDb
      .selectDatasetInstallMessages(datasetID)
      .any { it.status == InstallStatus.FailedInstallation || it.status == InstallStatus.FailedValidation }

    if (failed) {
      logger.info("skipping install-meta due to previous failures")
      return
    }

    appDB.withTransaction(target, plugin.type) {
      try {
        val record = it.getOrInsertDatasetRecord()

        // If the record in the app db has an isPublic flag value different from
        // what we expect, update the app db record.
        if (record.isPublic != (meta.visibility == DatasetVisibility.Public)) {
          it.updateDataset(record.copy(accessibility = meta.visibility))
        }

        logger.debug("upserting dataset meta record")
        it.upsertDatasetMeta(datasetID, meta)

        it.deleteDatasetPublications(datasetID)
        if (meta.publications.isNotEmpty())
          it.insertDatasetPublications(datasetID, meta.publications)

        it.deleteContacts(datasetID)
        if (meta.contacts.isNotEmpty())
          it.insertDatasetContacts(datasetID, meta.contacts)

        it.deleteExternalDatasetLinks(datasetID)
        if (meta.linkedDatasets.isNotEmpty())
          it.insertExternalDatasetLinks(datasetID, meta.linkedDatasets)

        it.deleteDatasetOrganisms(datasetID)
        if (meta.experimentalOrganism != null)
          it.insertExperimentalOrganism(datasetID, meta.experimentalOrganism!!)
        if (meta.hostOrganism != null)
          it.insertHostOrganism(datasetID, meta.hostOrganism!!)

        // Characteristics fields
        it.deleteCountries(datasetID)
        it.deleteSpecies(datasetID)
        it.deleteDiseases(datasetID)
        it.deleteAssociatedFactors(datasetID)
        it.deleteSampleTypes(datasetID)
        it.deleteCharacteristics(datasetID)
        meta.characteristics?.also { characteristics ->
          it.insertCharacteristics(datasetID, characteristics)
          it.insertCountries(datasetID, characteristics.countries)
          it.insertSpecies(datasetID, characteristics.studySpecies)
          it.insertDiseases(datasetID, characteristics.diseases)
          it.insertAssociatedFactors(datasetID, characteristics.associatedFactors)
          it.insertSampleTypes(datasetID, characteristics.sampleTypes)
        }

        // External identifier fields
        it.deleteDOIs(datasetID)
        it.deleteHyperlinks(datasetID)
        it.deleteBioprojectIDs(datasetID)
        meta.externalIdentifiers?.also { identifiers ->
          if (identifiers.dois.isNotEmpty())
            it.insertDOIs(datasetID, identifiers.dois)
          if (identifiers.hyperlinks.isNotEmpty())
            it.insertHyperlinks(datasetID, identifiers.hyperlinks)
          if (identifiers.bioprojectIDs.isNotEmpty())
            it.insertBioprojectIDs(datasetID, identifiers.bioprojectIDs)
        }

        it.deleteFundingAwards(datasetID)
        if (meta.funding.isNotEmpty())
          it.insertFundingAwards(datasetID, meta.funding)

        it.selectDatasetSyncControlRecord(datasetID) orElse {
          it.insertDatasetSyncControl(
            SyncControlRecord(
              datasetID = datasetID,
              sharesUpdated = OriginTimestamp,
              dataUpdated = OriginTimestamp,
              metaUpdated = OriginTimestamp,
            )
          )
        }
      } catch (e: Throwable) {
        logger.error("exception while attempting to getOrCreate app db records", e)
        it.rollback()
        throw e
      }
    }

    val sync = appDB.accessor(target, plugin.type)!!.selectDatasetSyncControlRecord(datasetID)!!

    if (!sync.metaUpdated.isBefore(metaTimestamp)) {
      logger.info("skipping install-meta; nothing has changed")
      return
    }

    appDB.withTransaction(target, plugin.type) {
      logger.debug("upserting install-meta message into app db")
      it.upsertInstallMetaMessage(datasetID, InstallStatus.Running)
    }

    try {
      plugin.client.postInstallMeta(eventID, datasetID, target, meta).use { result ->
        Metrics.MetaUpdates.count.labels(
          meta.type.name.toString(),
          meta.type.version,
          result.status.toString(),
        ).inc()

        try {
          when (result) {
            is EmptySuccessResponse    -> handleSuccessResponse()
            is ValidationErrorResponse -> handleValidationError(result)
            is ServiceErrorResponse    -> handleUnexpectedErrorResponse(result)
          }
        } catch (e: Throwable) {
          logger.info("install-meta request to handler server failed with exception:", e)
          appDB.withTransaction(target, plugin.type) {
            try {
              it.upsertInstallMetaMessage(datasetID, InstallStatus.FailedInstallation)
            } catch (e: SQLException) {
              if (e.errorCode == 1) {
                logger.info("unique key constraint violation on install meta; assuming race condition")
              } else {
                throw e
              }
            }
          }
          throw e
        } finally {
          appDB.withTransaction(target, plugin.type) { it.updateSyncControlMetaTimestamp(datasetID, metaTimestamp) }
        }
      }
    } catch (e: Throwable) {
      throw PluginRequestException.installMeta(plugin.name, target, ownerID, datasetID, cause = e)
    }
  }

  private fun UpdateMetaContext.WithPlugin.handleSuccessResponse() {
    logger.info("dataset meta installed successfully")

    appDB.withTransaction(target, plugin.type) {
      it.upsertInstallMetaMessage(
        datasetID,
        InstallStatus.Complete
      )
    }
  }

  private fun UpdateMetaContext.WithPlugin.handleValidationError(response: ValidationErrorResponse) {
    logger.info("dataset meta validation failed")

    appDB.withTransaction(target, plugin.type) {
      it.upsertInstallMetaMessage(
        datasetID,
        InstallStatus.FailedValidation,
        response.getWarningsSequence().joinToString("\n")
      )
    }
  }

  private fun UpdateMetaContext.WithPlugin.handleUnexpectedErrorResponse(res: ServiceErrorResponse) {
    logger.error("status ${res.status}: ${res.message}")

    throw PluginException.installMeta(plugin.name, target, ownerID, datasetID, res.message)
  }

  private fun AppDBTransaction.upsertInstallMetaMessage(
    datasetID: DatasetID,
    status: InstallStatus,
    message: String? = null,
  ) {
    val message = DatasetInstallMessage(datasetID, InstallType.Meta, status, message)
    upsertDatasetInstallMessage(message)
  }

  context(ctx: UpdateMetaContext.WithPlugin)
  private fun AppDBTransaction.getOrInsertDatasetRecord(): DatasetRecord {
    // Return the dataset if found
    selectDataset(ctx.datasetID)?.also { return it }

    ctx.logger.debug("inserting dataset record into app db")

    val record = DatasetRecord(ctx.datasetID, ctx.meta, PluginRegistry.categoryFor(ctx.meta.type), daysForApproval = -1)

    // this stuff is not project specific!
    //
    // NOTE: We don't install meta stuff here because we will be synchronizing
    // it immediately after this function call anyway.
    try {
      upsertDataset(record)
      insertDatasetVisibility(ctx.datasetID, ctx.meta.owner)
      insertDatasetDependencies(ctx.datasetID, ctx.meta.dependencies)

      upsertInstallMetaMessage(ctx.datasetID, InstallStatus.Running)

      logger.debug("inserting sync control record for dataset into app db")
      insertDatasetSyncControl(
        SyncControlRecord(
          datasetID = ctx.datasetID,
          sharesUpdated = OriginTimestamp,
          dataUpdated = OriginTimestamp,
          metaUpdated = OriginTimestamp,
        )
      )
    } catch (e: SQLException) {
      // Race condition: someone else inserted the record before us, which
      // should be fine.
      if (!isUniqueConstraintViolation(e))
        throw e
    }

    try {
      logger.debug("inserting dataset project link for dataset into app db")
      insertDatasetProjectLink(ctx.datasetID, ctx.target)
    } catch (e: SQLException) {
      if (!isUniqueConstraintViolation(e))
        throw e
    }

    return record
  }

  context(ctx: UpdateMetaContext)
  private fun DatasetDirectory.isUsable(): Boolean {
    if (!exists()) {
      ctx.logger.warn("ignoring update event; dataset with has no directory")
      return false
    }

    if (hasDeleteFlag()) {
      logger.info("ignoring update event; dataset has delete flag")
      return false
    }

    if (!hasMetaFile()) {
      logger.warn("ignoring update event; dataset has which has no $DatasetMetaFilename file")
      return false
    }

    return true
  }

  context(ctx: UpdateMetaContext)
  private fun CacheDB.initializeDataset(meta: DatasetMetadata) {
    openTransaction().use {

      // Insert a new dataset record
      it.tryInsertDataset(DatasetImpl(
        datasetID    = ctx.datasetID,
        type         = meta.type,
        ownerID      = meta.owner,
        isDeleted    = false,
        origin       = meta.origin,
        created      = meta.created,
        importStatus = DatasetImportStatus.Queued,
        inserted     = OffsetDateTime.now(),
      ))

      // insert metadata for the dataset
      it.tryInsertDatasetMeta(ctx.datasetID, meta)

      // Insert an import control record for the dataset
      it.tryInsertImportControl(ctx.datasetID, DatasetImportStatus.Queued)

      // insert project links for the dataset
      it.tryInsertDatasetProjects(ctx.datasetID, meta.installTargets)

      // insert a sync control record for the dataset using an old timestamp
      // that will predate any possible upload timestamp.
      it.initSyncControl(ctx.datasetID)
    }
  }

  private fun CacheDBTransaction.initSyncControl(datasetID: DatasetID) {
    tryInsertSyncControl(
      SyncControlRecord(
        datasetID     = datasetID,
        sharesUpdated = OriginTimestamp,
        dataUpdated   = OriginTimestamp,
        metaUpdated   = OriginTimestamp
      )
    )
  }
}
