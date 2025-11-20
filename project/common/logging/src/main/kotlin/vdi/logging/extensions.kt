@file:Suppress("NOTHING_TO_INLINE")
package vdi.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import vdi.model.EventID
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetType
import vdi.model.meta.InstallTargetID
import vdi.model.meta.UserID

const val PrefixDatasetID = "D"
const val PrefixDataType = "T"
const val PrefixEventID = "E"
const val PrefixInstallTarget = "P" // P for project
const val PrefixPluginName = "I" // I for installer
const val PrefixUserID = "O" // O for owner
const val PrefixScript = "S"

// region Logger Wrapping

inline fun Logger.mark(eventID: EventID) =
  MarkedLogger("$PrefixEventID=$eventID", this)

inline fun Logger.mark(eventID: EventID, datasetID: DatasetID) =
  MarkedLogger("$PrefixEventID=$eventID $PrefixDatasetID=$datasetID", this)

inline fun Logger.mark(eventID: EventID, ownerID: UserID, datasetID: DatasetID) =
  MarkedLogger(createLoggerMark(eventID, ownerID, datasetID), this)

inline fun Logger.mark(dataType: DatasetType, plugin: String) =
  MarkedLogger(createLoggerMark(dataType, plugin), this)

inline fun Logger.mark(dataType: DatasetType, plugin: String, target: InstallTargetID) =
  MarkedLogger(createLoggerMark(dataType, plugin) + " $PrefixInstallTarget=$target", this)

inline fun Logger.mark(ownerID: UserID, datasetID: DatasetID) =
  MarkedLogger(createLoggerMark(ownerID, datasetID), this)

inline fun Logger.mark(
  eventID: EventID? = null,
  ownerID: UserID? = null,
  datasetID: DatasetID? = null,
  dataType: DatasetType? = null,
  pluginName: String? = null,
  scriptName: String? = null,
  installTarget: InstallTargetID? = null,
) =
  MarkedLogger(
    createLoggerMark(eventID, ownerID, datasetID, dataType, pluginName, scriptName, installTarget),
    this,
  )

private inline fun String.add(prefix: String, value: Any?) =
  if (value != null)
    "$this $prefix=$value"
  else
    this

// endregion Logger Wrapping

// region Generic Logger Builder

inline fun logger(name: String): Logger =
  LoggerFactory.getLogger(name)

inline fun Any.markedLogger(mark: String): Logger =
  MarkedLogger(mark, javaClass)

inline fun Any.markedLogger(
  eventID: EventID? = null,
  ownerID: UserID? = null,
  datasetID: DatasetID? = null,
  dataType: DatasetType? = null,
  pluginName: String? = null,
  scriptName: String? = null,
  installTarget: InstallTargetID? = null,
) = LoggerFactory.getLogger(javaClass)
  .mark(eventID, ownerID, datasetID, dataType, pluginName, scriptName, installTarget)

inline fun Any.markedLogger(ownerID: UserID, datasetID: DatasetID): Logger =
  markedLogger(createLoggerMark(ownerID, datasetID))

inline fun Any.markedLogger(ownerID: UserID, datasetID: DatasetID, project: InstallTargetID): Logger =
  markedLogger(createLoggerMark(ownerID, datasetID, project))

inline fun Any.markedLogger(datasetID: DatasetID, project: InstallTargetID): Logger =
  markedLogger(createLoggerMark(datasetID, project))

inline fun Any.logger(): Logger =
  LoggerFactory.getLogger(this::class.java)

inline fun <reified T: Any> logger(): Logger =
  LoggerFactory.getLogger(T::class.java)

inline fun <reified T: Any> markedLogger(ownerID: UserID, datasetID: DatasetID): Logger =
  markedLogger<T>(createLoggerMark(ownerID, datasetID))

inline fun <reified T: Any> markedLogger(mark: String): Logger =
  MarkedLogger(mark, T::class)

// endregion Generic Logger Builder

// region String Building

inline fun createLoggerMark(dataType: DatasetType, pluginName: String) =
  "$PrefixDataType=$dataType $PrefixPluginName=$pluginName"

inline fun createLoggerMark(
  ownerID: UserID,
  datasetID: DatasetID,
  project: InstallTargetID,
) = "$PrefixUserID=$ownerID $PrefixDatasetID=$datasetID $PrefixInstallTarget=$project"

inline fun createLoggerMark(
  eventID: EventID,
  ownerID: UserID,
  datasetID: DatasetID,
) = "$PrefixEventID=$eventID $PrefixUserID=$ownerID $PrefixInstallTarget=$datasetID"

inline fun createLoggerMark(ownerID: UserID, datasetID: DatasetID) =
  "$PrefixUserID=$ownerID $PrefixDatasetID=$datasetID"

inline fun createLoggerMark(datasetID: DatasetID, project: InstallTargetID) =
  "$PrefixDatasetID=$datasetID $PrefixInstallTarget=$project"

fun createLoggerMark(
  eventID: EventID? = null,
  ownerID: UserID? = null,
  datasetID: DatasetID? = null,
  dataType: DatasetType? = null,
  pluginName: String? = null,
  scriptName: String? = null,
  installTarget: InstallTargetID? = null,
) = ""
  .add(PrefixEventID, eventID)
  .add(PrefixUserID, ownerID)
  .add(PrefixDatasetID, datasetID)
  .add(PrefixDataType, dataType)
  .add(PrefixPluginName, pluginName)
  .add(PrefixScript, scriptName)
  .add(PrefixInstallTarget, installTarget)

// endregion String Building