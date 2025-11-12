@file:Suppress("NOTHING_TO_INLINE")
package vdi.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import vdi.model.EventID
import vdi.model.data.DatasetID
import vdi.model.data.DatasetType
import vdi.model.data.InstallTargetID
import vdi.model.data.UserID

const val PrefixDatasetID = "D"
const val PrefixDataType = "T"
const val PrefixEventID = "E"
const val PrefixInstallTarget = "P" // P for project
const val PrefixPluginName = "I" // I for installer
const val PrefixUserID = "O" // O for owner

inline fun Any.logger(): Logger =
  LoggerFactory.getLogger(this::class.java)

inline fun logger(name: String): Logger =
  LoggerFactory.getLogger(name)

inline fun <reified T: Any> logger(): Logger =
  LoggerFactory.getLogger(T::class.java)

inline fun Any.markedLogger(mark: String): Logger =
  MarkedLogger(mark, javaClass)

inline fun Logger.mark(eventID: EventID) =
  MarkedLogger("$PrefixEventID=$eventID", this)

inline fun Logger.mark(eventID: EventID, datasetID: DatasetID) =
  MarkedLogger("$PrefixEventID=$eventID $PrefixDatasetID=$datasetID", this)

inline fun Logger.mark(eventID: EventID, ownerID: UserID, datasetID: DatasetID) =
  MarkedLogger("$PrefixEventID=$eventID $PrefixUserID=$ownerID $PrefixDatasetID=$datasetID", this)

inline fun Logger.mark(dataType: DatasetType, plugin: String) =
  MarkedLogger("$PrefixDataType=${dataType} $PrefixPluginName=$plugin", this)

inline fun Logger.mark(dataType: DatasetType, plugin: String, target: InstallTargetID) =
  MarkedLogger("$PrefixDataType=${dataType.name}:${dataType.version} $PrefixPluginName=$plugin $PrefixInstallTarget=$target", this)

inline fun Logger.mark(ownerID: UserID, datasetID: DatasetID) =
  MarkedLogger("$PrefixUserID=$ownerID $PrefixDatasetID=$datasetID", this)

inline fun createLoggerMark(ownerID: UserID, datasetID: DatasetID) =
  "$PrefixUserID=$ownerID $PrefixDatasetID=$datasetID"

inline fun Any.markedLogger(ownerID: UserID, datasetID: DatasetID): Logger =
  markedLogger(createLoggerMark(ownerID, datasetID))

inline fun createLoggerMark(ownerID: UserID, datasetID: DatasetID, project: InstallTargetID) =
  "$PrefixUserID=$ownerID $PrefixDatasetID=$datasetID P=$project"

inline fun Any.markedLogger(ownerID: UserID, datasetID: DatasetID, project: InstallTargetID): Logger =
  markedLogger(createLoggerMark(ownerID, datasetID, project))

inline fun createLoggerMark(datasetID: DatasetID, project: InstallTargetID) =
  "$PrefixDatasetID=$datasetID P=$project"

inline fun Any.markedLogger(datasetID: DatasetID, project: InstallTargetID): Logger =
  markedLogger(createLoggerMark(datasetID, project))

inline fun <reified T: Any> markedLogger(mark: String): Logger =
  MarkedLogger(mark, T::class)

inline fun <reified T: Any> markedLogger(ownerID: UserID, datasetID: DatasetID): Logger =
  markedLogger<T>("$PrefixUserID=$ownerID $PrefixDatasetID=$datasetID")
