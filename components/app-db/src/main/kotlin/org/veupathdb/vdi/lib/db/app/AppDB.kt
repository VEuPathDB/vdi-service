package org.veupathdb.vdi.lib.db.app

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.db.app.model.InstallStatuses
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

fun AppDB(): AppDB = AppDBImpl

interface AppDB {
  fun getDatasetStatuses(targets: Map<ProjectID, Collection<DatasetID>>): Map<DatasetID, Map<ProjectID, InstallStatuses>>

  fun getDatasetStatuses(target: DatasetID, projects: Collection<ProjectID>): Map<ProjectID, InstallStatuses>

  fun accessor(key: ProjectID): AppDBAccessor?

  fun transaction(key: ProjectID): AppDBTransaction?
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
inline fun <T> AppDB.withTransaction(projectID: ProjectID, fn: (AppDBTransaction) -> T): T {
  contract {
    callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
  }

  val trans = transaction(projectID)!!
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
