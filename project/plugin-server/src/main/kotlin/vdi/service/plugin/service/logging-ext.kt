@file:Suppress("NOTHING_TO_INLINE")

package vdi.service.plugin.service

import vdi.logging.createLoggerMark
import vdi.logging.markedLogger
import vdi.model.data.DatasetID
import vdi.model.data.InstallTargetID
import vdi.model.data.UserID
import vdi.service.plugin.server.context.ImportContext
import vdi.service.plugin.server.context.InstallDataContext
import vdi.service.plugin.server.context.InstallMetaContext
import vdi.service.plugin.server.context.UninstallDataContext

inline fun InstallDataContext.markedLogger() =
  markedLogger<InstallDataHandler>(createLoggerMark(ownerID, datasetID, installTarget) + " S=install-data")
inline fun InstallDataContext.metaLogger() =
  markedLogger<InstallDataHandler>(createLoggerMark(ownerID, datasetID, installTarget) + " S=install-meta")
inline fun InstallDataContext.compatLogger() =
  markedLogger<InstallDataHandler>(createLoggerMark(ownerID, datasetID, installTarget) + " S=check-compatibility")


inline fun InstallMetaContext.markedLogger() =
  markedLogger<InstallMetaHandler>(createLoggerMark(ownerID, datasetID, installTarget) + " S=install-meta")

inline fun UninstallDataContext.markedLogger() =
  markedLogger<UninstallDataHandler>(createLoggerMark(datasetID, installTarget) + "S=uninstall-data")

inline fun ImportContext.markedLogger() =
  markedLogger<ImportHandler>(createLoggerMark(ownerID, datasetID) + " S=import")

inline fun <reified T: Any> markedLogger(
  ownerID: UserID,
  datasetID: DatasetID,
  script: String,
) = markedLogger<T>(createLoggerMark(ownerID, datasetID) + " S=$script")

inline fun <reified T: Any> markedLogger(
  ownerID: UserID,
  datasetID: DatasetID,
  installTarget: InstallTargetID,
  script: String,
) = markedLogger<T>(createLoggerMark(ownerID, datasetID, installTarget) + " S=$script")