@file:JvmName("AppDbProvider")
package vdi.core.db.app

import vdi.model.meta.DatasetType
import vdi.model.meta.InstallTargetID

@JvmName("appDb")
fun AppDB(): AppDB = AppDBImpl

@JvmName("appDb")
fun AppDB(installTarget: InstallTargetID, dataType: DatasetType) = AppDBImpl.accessor(installTarget, dataType)
