@file:JvmName("AppDbManager")
package vdi.core.db.app

import vdi.model.meta.DatasetType
import vdi.model.meta.InstallTargetID

@JvmName("getInstance")
fun AppDB(): AppDB = AppDBImpl

fun AppDB(installTarget: InstallTargetID, dataType: DatasetType) = AppDBImpl.accessor(installTarget, dataType)
