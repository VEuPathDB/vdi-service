package vdi.lib.db.app

import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import vdi.lib.db.app.model.InstallStatuses

fun AppDB(): AppDB = AppDBImpl

interface AppDB {
  fun getDatasetStatuses(targets: Map<ProjectID, Collection<DatasetID>>): Map<DatasetID, Map<ProjectID, InstallStatuses>>

  fun getDatasetStatuses(target: DatasetID, projects: Collection<ProjectID>): Map<ProjectID, InstallStatuses>

  fun accessor(key: ProjectID, dataType: DataType): AppDBAccessor?

  fun transaction(key: ProjectID, dataType: DataType): AppDBTransaction?
}
