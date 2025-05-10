package vdi.component.db.app

import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import vdi.component.db.app.model.InstallStatuses
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

fun AppDB(): AppDB = AppDBImpl

const val UniqueConstraintViolation = 1

interface AppDB {
  fun getDatasetStatuses(targets: Map<ProjectID, Collection<DatasetID>>): Map<DatasetID, Map<ProjectID, InstallStatuses>>

  fun getDatasetStatuses(target: DatasetID, projects: Collection<ProjectID>): Map<ProjectID, InstallStatuses>

  fun accessor(key: ProjectID, dataType: DataType): AppDBAccessor?

  fun transaction(key: ProjectID, dataType: DataType): AppDBTransaction?
}

/**
 * Executes the given function ([fn]) in the context of a database
 * transaction, committing or rolling back the transaction automatically
 * depending on whether the function passes up an uncaught exception.
 *
 * If the function [fn] executes successfully, the transaction will be
 * committed on function return.
 *
 * If the function [fn] throws an exception, the transaction will be rolled
 * back and the exception will be rethrown.
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
