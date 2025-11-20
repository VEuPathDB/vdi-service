package vdi.core.db.app

import org.slf4j.Logger
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import vdi.core.db.app.model.DatasetInstallMessage
import vdi.core.db.app.model.DatasetRecord
import vdi.core.db.app.model.InstallStatus
import vdi.core.db.app.model.InstallType
import vdi.core.db.model.SyncControlRecord
import vdi.model.OriginTimestamp
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetMetadata
import vdi.model.meta.DatasetType
import vdi.model.meta.InstallTargetID


/**
 * Executes the given function in the context of a database transaction,
 * committing or rolling back the transaction automatically depending on whether
 * the function passes up an uncaught exception.
 *
 * If the function executes successfully, the transaction will be committed on
 * completion.
 *
 * If the function throws an exception, the transaction will be rolled back and
 * the exception will be rethrown.
 *
 * Regardless of the result, the connection will be closed before this method
 * returns.
 *
 * @param T Type of the value returned by the given function [fn].
 *
 * @param installTarget ID of the project for which the transaction should be
 * opened.
 *
 * @param fn Function that will be executed in the context of a database
 * transaction.
 *
 * @return The value returned by the given function [fn].
 */
@OptIn(ExperimentalContracts::class)
inline fun <T> AppDB.withTransaction(installTarget: InstallTargetID, dataType: DatasetType, fn: (AppDBTransaction) -> T): T {
  contract {
    callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
  }

  val trans = transaction(installTarget, dataType)!!
  val out: T

  try {
    out = fn(trans)
    trans.commit()
  } catch (e: Throwable) {
    trans.rollback()
    throw e
  } finally {
    trans.close()
  }

  return out
}

/**
 * Executes the given callback function for every dataset type registered for a
 * target project.
 *
 * @param installTarget ID of the project for which registered dataset types should
 * be iterated over.
 *
 * @param fn Function that will be executed for every data type registered for a
 * target dataset.
 */
inline fun AppDB.accessorForEachDataType(installTarget: InstallTargetID, fn: AppDBAccessor.(DatasetType) -> Unit) {
  for (entry in AppDatabaseRegistry[installTarget]!!)
    accessor(installTarget, entry.first)!!.fn(entry.first)
}

/**
 * Executes the given callback function for every dataset type registered for a
 * target project.
 *
 * @param installTarget ID of the project for which registered dataset types should
 * be iterated over.
 *
 * @param fn Function that will be executed for every data type registered for a
 * target dataset.
 */
inline fun AppDB.transactionForEachDataType(installTarget: InstallTargetID, fn: AppDBTransaction.(DatasetType) -> Unit) {
  for (entry in AppDatabaseRegistry[installTarget]!!)
    transaction(installTarget, entry.first)!!.fn(entry.first)
}

fun AppDBTransaction.purgeDatasetControlTables(datasetID: DatasetID) {
  deleteAssociatedFactors(datasetID)
  deleteBioprojectIDs(datasetID)
  deleteCharacteristics(datasetID)
  deleteContacts(datasetID)
  deleteCountries(datasetID)
  deleteDependencies(datasetID)
  deleteDiseases(datasetID)
  deleteDOIs(datasetID)
  deleteFundingAwards(datasetID)
  deleteHyperlinks(datasetID)
  deleteInstallMessages(datasetID)
  deleteExternalDatasetLinks(datasetID)
  deleteDatasetMeta(datasetID)
  deleteDatasetOrganisms(datasetID)
  deleteDatasetProjectLinks(datasetID)
  deleteDatasetPublications(datasetID)
  deleteSampleTypes(datasetID)
  deleteSpecies(datasetID)
  deleteDatasetVisibilities(datasetID)
  deleteDataset(datasetID)
}

context(logger: Logger)
fun AppDBTransaction.upsertDatasetRecord(record: DatasetRecord, meta: DatasetMetadata) {
  upsertDataset(record)
  insertDatasetVisibility(record.datasetID, meta.owner)
  insertDatasetDependencies(record.datasetID, meta.dependencies)

  upsertDatasetInstallMessage(DatasetInstallMessage(record.datasetID, InstallType.Meta, InstallStatus.Running, null))

  logger.debug("inserting sync control record for dataset into app db")
  insertDatasetSyncControl(
    SyncControlRecord(
      datasetID = record.datasetID,
      sharesUpdated = OriginTimestamp,
      dataUpdated = OriginTimestamp,
      metaUpdated = OriginTimestamp,
    )
  )
}

@Suppress("NOTHING_TO_INLINE")
inline fun AppDBTransaction.isUniqueConstraintViolation(e: Throwable) =
  platform.isUniqueConstraintViolation(e)
