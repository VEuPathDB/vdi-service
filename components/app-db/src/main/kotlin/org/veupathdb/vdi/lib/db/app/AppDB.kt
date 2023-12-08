package org.veupathdb.vdi.lib.db.app

import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.db.app.sql.selectInstallStatuses
import org.veupathdb.vdi.lib.db.app.model.InstallStatuses
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract


object AppDB {

  private val log = LoggerFactory.getLogger(javaClass)

  fun getDatasetStatuses(targets: Map<ProjectID, Collection<DatasetID>>): Map<DatasetID, Map<ProjectID, InstallStatuses>> {
    log.trace("getDatasetStatuses(targets={})", targets)

    val result = HashMap<DatasetID, MutableMap<ProjectID, InstallStatuses>>(100)

    for ((projectID, datasetIDs) in targets) {
      val ds = AppDatabaseRegistry.require(projectID)

      ds.source.connection.use { con ->
        con.selectInstallStatuses(ds.ctlSchema, datasetIDs)
          .forEach { (datasetID, statuses) ->
            result.computeIfAbsent(datasetID) { HashMap() } [projectID] = statuses
          }
      }
    }

    return result
  }

  fun getDatasetStatuses(target: DatasetID, projects: Collection<ProjectID>): Map<ProjectID, InstallStatuses> {
    log.trace("getDatasetStatuses(target={}, projects={})", target, projects)

    val out = HashMap<ProjectID, InstallStatuses>(projects.size)

    for (projectID in projects) {
      val ds = AppDatabaseRegistry.require(projectID)

      ds.source.connection.use { con -> out[projectID] = con.selectInstallStatuses(ds.ctlSchema, target) }
    }

    return out
  }

  fun accessor(key: ProjectID): AppDBAccessor =
    AppDatabaseRegistry.require(key)
      .let { AppDBAccessorImpl(it.ctlSchema, it.source) }

  fun transaction(key: ProjectID): AppDBTransaction =
    AppDatabaseRegistry.require(key)
      .let { ds -> AppDBTransactionImpl(ds.ctlSchema, ds.source.connection.also { it.autoCommit = false }) }

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
  fun <T> withTransaction(projectID: ProjectID, fn: (AppDBTransaction) -> T): T {
    contract {
      callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }

    log.trace("withTransaction(projectID={}, fn=...)", projectID)

    val trans = transaction(projectID)
    val out: T

    try {
      out = fn(trans)
      trans.commit()
    } catch (e: Throwable) {
      log.error("transaction for project ID $projectID failed with exception", e)
      trans.rollback()
      throw e
    } finally {
      trans.close()
    }

    return out
  }

}

