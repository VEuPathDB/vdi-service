@file:Suppress("NOTHING_TO_INLINE")

package vdi.service.plugin.service

import org.slf4j.Logger
import vdi.logging.createLoggerMark
import vdi.logging.markedLogger
import vdi.model.meta.DatasetID
import vdi.model.meta.InstallTargetID
import vdi.model.meta.UserID
import vdi.service.plugin.process.preprocess.ImportContext
import vdi.service.plugin.process.preprocess.ImportHandler
import vdi.service.plugin.process.install.data.InstallDataContext
import vdi.service.plugin.process.install.data.InstallDataHandler
import vdi.service.plugin.process.install.meta.InstallMetaContext
import vdi.service.plugin.process.install.meta.InstallMetaHandler
import vdi.service.plugin.process.uninstall.UninstallDataContext
import vdi.service.plugin.process.uninstall.UninstallDataHandler

inline fun InstallDataContext.makeLogger(scriptName: String): Logger =
  markedLogger<InstallDataHandler>(createLoggerMark(
    eventID = eventID,
    ownerID = ownerID,
    datasetID = datasetID,
    dataType = metadata.type,
    scriptName = scriptName,
    installTarget = installTarget,
  ))

inline fun InstallDataContext.dataLogger() = makeLogger("install-data")
inline fun InstallDataContext.metaLogger() = makeLogger("install-meta")
inline fun InstallDataContext.compatLogger() = makeLogger("check-compatibility")


inline fun InstallMetaContext.makeLogger() =
  makeLogger<InstallMetaHandler>(createLoggerMark(ownerID, datasetID, installTarget) + " S=install-meta")

inline fun UninstallDataContext.makeLogger() =
  makeLogger<UninstallDataHandler>(createLoggerMark(datasetID, installTarget) + "S=uninstall-data")

inline fun ImportContext.makeLogger() =
  makeLogger<ImportHandler>(createLoggerMark(ownerID, datasetID) + " S=import")

inline fun <reified T: Any> makeLogger(
  ownerID: UserID,
  datasetID: DatasetID,
  script: String,
) = makeLogger<T>(createLoggerMark(ownerID, datasetID) + " S=$script")

inline fun <reified T: Any> makeLogger(
  ownerID: UserID,
  datasetID: DatasetID,
  installTarget: InstallTargetID,
  script: String,
) = makeLogger<T>(createLoggerMark(ownerID, datasetID, installTarget) + " S=$script")