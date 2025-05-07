package vdi.lib.db.app

import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


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
 * @param projectID ID of the project for which the transaction should be
 * opened.
 *
 * @param fn Function that will be executed in the context of a database
 * transaction.
 *
 * @return The value returned by the given function [fn].
 */
@OptIn(ExperimentalContracts::class)
inline fun <T> AppDB.withTransaction(projectID: ProjectID, dataType: DataType, fn: (AppDBTransaction) -> T): T {
  contract {
    callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
  }

  val trans = transaction(projectID, dataType)!!
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
 * @param projectID ID of the project for which registered dataset types should
 * be iterated over.
 *
 * @param fn Function that will be executed for every data type registered for a
 * target dataset.
 */
inline fun AppDB.accessorForEachDataType(projectID: ProjectID, fn: AppDBAccessor.(DataType) -> Unit) {
  for (entry in AppDatabaseRegistry[projectID]!!)
    accessor(projectID, entry.first)!!.fn(entry.first)
}

/**
 * Executes the given callback function for every dataset type registered for a
 * target project.
 *
 * @param projectID ID of the project for which registered dataset types should
 * be iterated over.
 *
 * @param fn Function that will be executed for every data type registered for a
 * target dataset.
 */
inline fun AppDB.transactionForEachDataType(projectID: ProjectID, fn: AppDBTransaction.(DataType) -> Unit) {
  for (entry in AppDatabaseRegistry[projectID]!!)
    transaction(projectID, entry.first)!!.fn(entry.first)
}

fun AppDBTransaction.purgeDatasetControlTables(datasetID: DatasetID) {
  deleteDatasetVisibilities(datasetID)
  deleteDatasetProjectLinks(datasetID)
  deleteSyncControl(datasetID)
  deleteInstallMessages(datasetID)
  deleteDatasetContacts(datasetID)
  deleteDatasetHyperlinks(datasetID)
  deleteDatasetPublications(datasetID)
  deleteDatasetOrganisms(datasetID)
  deleteDatasetContacts(datasetID)
  deleteDatasetDependencies(datasetID)
  deleteDatasetProperties(datasetID)
  deleteDatasetMeta(datasetID)
  deleteDataset(datasetID)
}

@Suppress("NOTHING_TO_INLINE")
inline fun AppDBTransaction.isUniqueConstraintViolation(e: Throwable) =
  platform.isUniqueConstraintViolation(e)
