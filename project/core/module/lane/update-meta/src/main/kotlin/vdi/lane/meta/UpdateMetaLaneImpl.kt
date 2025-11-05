package vdi.lane.meta

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.sql.SQLException
import java.time.OffsetDateTime
import java.util.concurrent.ConcurrentHashMap
import vdi.core.db.app.AppDB
import vdi.core.db.app.AppDBTransaction
import vdi.core.db.app.isUniqueConstraintViolation
import vdi.core.db.app.model.*
import vdi.core.db.app.withTransaction
import vdi.logging.logger
import vdi.core.metrics.Metrics
import vdi.core.util.orElse
import vdi.core.async.WorkerPool
import vdi.core.db.cache.CacheDB
import vdi.core.db.cache.CacheDBTransaction
import vdi.core.db.cache.model.DatasetImpl
import vdi.core.db.cache.model.DatasetImportStatus
import vdi.core.db.cache.withTransaction
import vdi.core.db.model.SyncControlRecord
import vdi.core.kafka.EventMessage
import vdi.core.kafka.EventSource
import vdi.core.kafka.router.KafkaRouter
import vdi.core.modules.AbortCB
import vdi.core.modules.AbstractVDIModule
import vdi.core.plugin.client.PluginException
import vdi.core.plugin.client.PluginRequestException
import vdi.core.plugin.client.response.inm.InstallMetaResponseType
import vdi.core.plugin.client.response.inm.InstallMetaUnexpectedErrorResponse
import vdi.core.plugin.mapping.PluginHandler
import vdi.core.plugin.mapping.PluginHandlers
import vdi.core.plugin.registry.PluginRegistry
import vdi.core.s3.DatasetDirectory
import vdi.core.s3.DatasetObjectStore
import vdi.model.DatasetMetaFilename
import vdi.model.OriginTimestamp
import vdi.model.data.*

internal class UpdateMetaLaneImpl(
  private val config: UpdateMetaLaneConfig,
  abortCB: AbortCB,
)
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
        while (!isShutDown()) {
          kc.fetchMessages(config.eventKey)
            .forEach {
              logger.info("Received install-meta job for dataset {}/{} from source {}", it.userID, it.datasetID, it.eventSource)
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
      logger.info("meta update already in progress for dataset {}/{}, ignoring meta event", msg.userID, msg.datasetID)
    }
  }

  private suspend fun tryUpdateMeta(dm: DatasetObjectStore, kr: KafkaRouter, msg: EventMessage) {
    try {
      if (msg.eventSource == EventSource.UpdateMetaLane) {
        logger.warn("attempted to recurse update meta on dataset ${msg.userID}/${msg.datasetID}")
      } else {
        updateMeta(dm, msg.userID, msg.datasetID)
        kr.sendReconciliationTrigger(msg.userID, msg.datasetID, EventSource.UpdateMetaLane)
      }
    } catch (e: PluginException) {
      e.log(logger::error)
    } catch (e: Throwable) {
      PluginException.installMeta("N/A", "N/A", msg.userID, msg.datasetID, cause = e).log(logger::error)
    }
  }

  private suspend fun updateMeta(dm: DatasetObjectStore, userID: UserID, datasetID: DatasetID) {
    logger.debug("Looking up dataset directory for dataset {}/{}", userID, datasetID)

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
    logger.info("dataset {}/{} meta timestamp is {}", userID, datasetID, metaTimestamp)

    val timer = Metrics.MetaUpdates.duration
      .labels(datasetMeta.type.name.toString(), datasetMeta.type.version)
      .startTimer()

    // Attempt to select the dataset details from the cache DB
    val cachedDataset = cacheDB.selectDataset(datasetID)

    // If no dataset record was found in the cache DB then this is (likely) the
    // first event for the dataset coming through the pipeline.
    if (cachedDataset == null) {
      logger.debug("dataset details were not found for dataset {}/{}, creating them", userID, datasetID)
      // If they were not found, construct them
      cacheDB.initializeDataset(datasetID, datasetMeta)
    }

    // Else if the dataset is marked as deleted already, then bail here.
    else {
      if (cachedDataset.isDeleted) {
        logger.info("refusing to update dataset {}/{} meta due to it being marked as deleted", userID, datasetID)
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
      logger.error("dataset {}/{} declares a type of {}:{} which is unknown to the vdi service", userID, datasetID, datasetMeta.type.name, datasetMeta.type.version)
      return
    }

    datasetMeta.installTargets
      .forEach { projectID -> tryUpdateTargetMeta(ph, datasetMeta, metaTimestamp, datasetID, projectID, userID) }

    timer.observeDuration()
  }

  private suspend fun tryUpdateTargetMeta(
    ph: PluginHandler,
    meta: DatasetMetadata,
    metaTimestamp: OffsetDateTime,
    datasetID: DatasetID,
    installTarget: InstallTargetID,
    userID: UserID,
  ) {
    try {
      updateTargetMeta(ph, meta, metaTimestamp, datasetID, installTarget, userID)
    } catch (e: PluginException) {
      throw e
    } catch (e: Throwable) {
      throw PluginException.installMeta(ph.name, installTarget, userID, datasetID, cause = e)
    }
  }

  private suspend fun updateTargetMeta(
    ph: PluginHandler,
    meta: DatasetMetadata,
    metaTimestamp: OffsetDateTime,
    datasetID: DatasetID,
    installTarget: InstallTargetID,
    userID: UserID,
  ) {
    if (!ph.appliesToProject(installTarget)) {
      logger.warn("dataset {}/{} declares a project id of {} which is not applicable to dataset type {}:{}", userID, datasetID, installTarget, meta.type.name, meta.type.version)
      return
    }

    val appDb = appDB.accessor(installTarget, ph.type) orElse {
      logger.info("skipping dataset {}/{}, project {} update meta due to target being disabled", userID, datasetID, installTarget)
      return
    }

    val failed = appDb
      .selectDatasetInstallMessages(datasetID)
      .any { it.status == InstallStatus.FailedInstallation || it.status == InstallStatus.FailedValidation }

    if (failed) {
      logger.info("skipping install-meta for dataset {}/{}, project {} due to previous failures", userID, datasetID, installTarget)
      return
    }

    // Record types to upsert:
    // * dataset_bioproject_id
    // * dataset_country
    // * dataset_disease
    // * dataset_doi
    // * dataset_funding_award
    // * dataset_hyperlink
    // * dataset_link
    // * dataset_project
    // * dataset_sample_type
    // * dataset_species
    appDB.withTransaction(installTarget, ph.type) {
      try {
        // "constant" records created in this call:
        // * dataset
        // * dataset_meta
        // * dataset_dependencies
        // * dataset_visibility (owner id record only)
        val record = it.getOrInsertDatasetRecord(userID, datasetID, installTarget, meta)

        // If the record in the app db has an isPublic flag value different from
        // what we expect, update the app db record.
        if (record.isPublic != (meta.visibility == DatasetVisibility.Public)) {
          it.updateDataset(record.copy(accessibility = meta.visibility))
        }

        logger.debug("upserting dataset meta record for dataset {}/{} into app db for project {}", userID, datasetID, installTarget)
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
          it.insertDatasetSyncControl(SyncControlRecord(
            datasetID     = datasetID,
            sharesUpdated = OriginTimestamp,
            dataUpdated   = OriginTimestamp,
            metaUpdated   = OriginTimestamp,
          ))
        }
      } catch (e: Throwable) {
        logger.error("exception while attempting to getOrCreate app db records for dataset $userID/$datasetID", e)
        it.rollback()
        throw e
      }
    }

    val sync = appDB.accessor(installTarget, ph.type)!!.selectDatasetSyncControlRecord(datasetID)!!

    if (!sync.metaUpdated.isBefore(metaTimestamp)) {
      logger.info("skipping install-meta for dataset {}/{}, project {} as nothing has changed.", userID, datasetID, installTarget)
      return
    }

    appDB.withTransaction(installTarget, ph.type) {
      logger.debug("upserting install-meta message for dataset {}/{} into app db for project {}", userID, datasetID, installTarget)
      it.upsertInstallMetaMessage(datasetID, InstallStatus.Running)
    }

    val result = try {
      ph.client.postInstallMeta(datasetID, installTarget, meta)
    } catch (e: Throwable){
      throw PluginRequestException.installMeta(ph.name, installTarget, userID, datasetID, cause = e)
    }

    Metrics.MetaUpdates.count.labels(meta.type.name.toString(), meta.type.version, result.responseCode.toString()).inc()

    try {
      when (result.type) {
        InstallMetaResponseType.Success -> handleSuccessResponse(ph, userID, datasetID, installTarget)

        InstallMetaResponseType.BadRequest -> handleBadRequestResponse(
          ph,
          userID,
          datasetID,
          installTarget,
          result as vdi.core.plugin.client.response.inm.InstallMetaBadRequestResponse,
        )

        InstallMetaResponseType.UnexpectedError -> handleUnexpectedErrorResponse(
          ph,
          userID,
          datasetID,
          installTarget,
          result as InstallMetaUnexpectedErrorResponse,
        )
      }
    } catch (e: Throwable) {
      logger.info("install-meta request to handler server failed with exception:", e)
      appDB.withTransaction(installTarget, ph.type) {
        try {
          it.upsertInstallMetaMessage(datasetID, InstallStatus.FailedInstallation)
        } catch (e: SQLException) {
          if (e.errorCode == 1) {
            logger.info("unique key constraint violation on dataset {}/{} install meta, assuming race condition.", userID, datasetID)
          } else {
            throw e
          }
        }
      }
      throw e
    } finally {
      appDB.withTransaction(installTarget, ph.type) { it.updateSyncControlMetaTimestamp(datasetID, metaTimestamp) }
    }
  }

  private fun handleSuccessResponse(handler: PluginHandler, userID: UserID, datasetID: DatasetID, installTarget: InstallTargetID) {
    logger.info(
      "dataset handler server reports dataset {}/{} meta installed successfully into project {} via plugin {}",
      userID,
      datasetID,
      installTarget,
      handler,
    )

    appDB.withTransaction(installTarget, handler.type) { it.upsertInstallMetaMessage(datasetID, InstallStatus.Complete) }
  }

  private fun AppDBTransaction.upsertInstallMetaMessage(datasetID: DatasetID, status: InstallStatus) {
    val message = DatasetInstallMessage(datasetID, InstallType.Meta, status, null)
    upsertDatasetInstallMessage(message)
  }

  private fun AppDBTransaction.getOrInsertDatasetRecord(
    userID: UserID,
    datasetID: DatasetID,
    installTarget: InstallTargetID,
    meta: DatasetMetadata,
  ): DatasetRecord {
    // Return the dataset if found
    selectDataset(datasetID)?.also { return it }

    logger.debug("inserting dataset record for dataset {}/{} into app db for project {}", userID, datasetID, installTarget)

    val record = DatasetRecord(
      datasetID       = datasetID,
      meta            = meta,
      category        = PluginRegistry.categoryFor(meta.type),
      daysForApproval = -1,
    )

    // this stuff is not project specific!
    //
    // NOTE: We don't install meta stuff here because we will be synchronizing
    // it immediately after this function call anyway.
    try {
      upsertDataset(record)
      insertDatasetVisibility(datasetID, meta.owner)
      insertDatasetDependencies(datasetID, meta.dependencies)

      upsertInstallMetaMessage(datasetID, InstallStatus.Running)

      logger.debug("inserting sync control record for dataset {}/{} into app db for project {}", userID, datasetID, installTarget)
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
      logger.debug("inserting dataset project link for dataset {}/{} into app db for project {}", userID, datasetID, installTarget)
      insertDatasetProjectLink(datasetID, installTarget)
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
    installTarget: InstallTargetID,
    res: vdi.core.plugin.client.response.inm.InstallMetaBadRequestResponse
  ) {
    logger.error(
      "dataset handler server reports 400 error for meta-install on dataset {}/{}, project {} via plugin {}",
      userID,
      datasetID,
      installTarget,
      handler.name,
    )

    throw PluginException.installMeta(handler.name, installTarget, userID, datasetID, res.message)
  }

  private fun handleUnexpectedErrorResponse(
    handler: PluginHandler,
    userID: UserID,
    datasetID: DatasetID,
    installTarget: InstallTargetID,
    res: InstallMetaUnexpectedErrorResponse,
  ) {
    logger.error(
      "dataset handler server reports 500 error for meta-install on dataset {}/{}, project {} via plugin {}",
      userID,
      datasetID,
      installTarget,
      handler.name
    )

    throw PluginException.installMeta(handler.name, installTarget, userID, datasetID, res.message)
  }

  private fun DatasetDirectory.isUsable(userID: UserID, datasetID: DatasetID): Boolean {
    if (!exists()) {
      logger.warn("got an update-meta event for dataset {}/{} which has no directory?", userID, datasetID)
      return false
    }

    if (hasDeleteFlag()) {
      logger.info("got an update-meta event for dataset {}/{} which has a delete flag, ignoring it.", userID, datasetID)
      return false
    }

    if (!hasMetaFile()) {
      logger.warn("got an update-meta event for dataset {}/{} which has no {} file?", userID, datasetID, DatasetMetaFilename)
      return false
    }

    return true
  }

  private fun CacheDB.initializeDataset(datasetID: DatasetID, meta: DatasetMetadata) {
    openTransaction().use {

      // Insert a new dataset record
      it.tryInsertDataset(DatasetImpl(
        datasetID    = datasetID,
        type         = meta.type,
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
      it.tryInsertDatasetProjects(datasetID, meta.installTargets)

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
