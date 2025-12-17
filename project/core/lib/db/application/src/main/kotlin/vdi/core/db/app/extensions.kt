package vdi.core.db.app

import java.time.OffsetDateTime
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import vdi.core.db.app.model.DatasetRecord
import vdi.core.db.app.model.DeleteFlag
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
 * Remove all app-db records for a dataset for uninstallation or pruning.
 *
 * @param datasetID ID of the dataset to remove.
 *
 * @param flag If set, the root dataset record will not be deleted, but will
 * instead have its soft-delete flag updated to the given value.
 */
fun AppDBTransaction.purgeDatasetControlTables(datasetID: DatasetID, flag: DeleteFlag? = null) {
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
  deletePublications(datasetID)
  deleteSampleTypes(datasetID)
  deleteSpecies(datasetID)
  deleteDatasetVisibilities(datasetID)
  deleteSyncControl(datasetID)
  if (flag != null)
    updateDatasetDeletedFlag(datasetID, flag)
  else
    deleteDataset(datasetID)
}

fun AppDBTransaction.upsertDatasetRecord(
  record:        DatasetRecord,
  meta:          DatasetMetadata,
  metaTimestamp: OffsetDateTime,
) {
  val (datasetID) = record

  // Actions written in the same order that the table defs appear in the DDL for
  // the AppDB schema.
  upsertDataset(record)
  upsertDatasetMeta(datasetID, meta)
  upsertSyncControlMetaTimestamp(datasetID, metaTimestamp)
  // upsertDatasetInstallMessage() -- we don't have install messages yet
  upsertDatasetVisibility(datasetID, meta.owner)
  tryInsertDatasetProjectLink(datasetID)
  // upsertDatasetInstallActivity() -- we aren't installing here
  replace(datasetID, meta.dependencies, ::deleteDependencies, ::insertDependencies)
  replace(datasetID, meta.publications, ::deletePublications, ::insertPublications)
  replace(datasetID, meta.externalIdentifiers?.hyperlinks, ::deleteHyperlinks, ::insertHyperlinks)
  replace(datasetID, meta.contacts, ::deleteContacts, ::insertContacts)

  deleteDatasetOrganisms(datasetID)
  if (meta.experimentalOrganism != null)
    insertExperimentalOrganism(datasetID, meta.experimentalOrganism!!)
  if (meta.hostOrganism != null)
    insertHostOrganism(datasetID, meta.hostOrganism!!)

  replace(datasetID, meta.funding, ::deleteFundingAwards, ::insertFundingAwards)

  deleteCharacteristics(datasetID)
  meta.studyCharacteristics?.also { characteristics -> insertCharacteristics(datasetID, characteristics) }

  replace(datasetID, meta.studyCharacteristics?.countries, ::deleteCountries, ::insertCountries)
  replace(datasetID, meta.studyCharacteristics?.studySpecies, ::deleteSpecies, ::insertSpecies)
  replace(datasetID, meta.studyCharacteristics?.diseases, ::deleteDiseases, ::insertDiseases)
  replace(datasetID, meta.studyCharacteristics?.associatedFactors, ::deleteAssociatedFactors, ::insertAssociatedFactors)
  replace(datasetID, meta.studyCharacteristics?.sampleTypes, ::deleteSampleTypes, ::insertSampleTypes)
  replace(datasetID, meta.externalIdentifiers?.dois, ::deleteDOIs, ::insertDOIs)
  replace(datasetID, meta.externalIdentifiers?.bioprojectIDs, ::deleteBioprojectIDs, ::insertBioprojectIDs)
  replace(datasetID, meta.linkedDatasets, ::deleteExternalDatasetLinks, ::insertExternalDatasetLinks)
}

@Suppress("NOTHING_TO_INLINE")
inline fun AppDBTransaction.isUniqueConstraintViolation(e: Throwable) =
  platform.isUniqueConstraintViolation(e)

@OptIn(ExperimentalContracts::class)
private inline fun <T: Any> replace(
  datasetID: DatasetID,
  rows: Collection<T>?,
  delete: (DatasetID) -> Any?,
  insert: (DatasetID, Iterable<T>) -> Any?,
) {
  contract {
    callsInPlace(delete, InvocationKind.EXACTLY_ONCE)
    callsInPlace(insert, InvocationKind.AT_MOST_ONCE)
  }

  delete(datasetID)
  if (rows?.isNotEmpty() == true)
    insert(datasetID, rows)
}