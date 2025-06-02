package vdi.core.db.app

import vdi.core.db.app.model.InstallStatuses
import vdi.model.data.DatasetID
import vdi.model.data.DatasetType
import vdi.model.data.InstallTargetID

interface AppDB {
  fun getDatasetStatuses(targets: Map<InstallTargetID, Collection<DatasetID>>): Map<DatasetID, Map<InstallTargetID, InstallStatuses>>

  fun getDatasetStatuses(target: DatasetID, projects: Collection<InstallTargetID>): Map<InstallTargetID, InstallStatuses>

  fun accessor(key: InstallTargetID, dataType: DatasetType): AppDBAccessor?

  fun transaction(key: InstallTargetID, dataType: DatasetType): AppDBTransaction?
}
