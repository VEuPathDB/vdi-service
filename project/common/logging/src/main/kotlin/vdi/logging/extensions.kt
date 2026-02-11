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
const val PrefixRequestID = "R"
const val PrefixRequestURI = "U"

// region Logger Wrapping

inline fun Logger.mark(marks: Array<String>) =
  when (this) {
    is MarkedLogger -> copy(marks)
    else            -> MarkedLogger(marks, this)
  }

inline fun Logger.mark(eventID: EventID) =
  mark(arrayOf("$PrefixEventID=$eventID"))

inline fun Logger.mark(eventID: EventID, datasetID: DatasetID) =
  mark(arrayOf("$PrefixEventID=$eventID", "$PrefixDatasetID=$datasetID"))

inline fun Logger.mark(eventID: EventID, ownerID: UserID, datasetID: DatasetID) =
  mark(createLoggerMark(eventID = eventID, ownerID = ownerID, datasetID = datasetID))

inline fun Logger.mark(dataType: DatasetType, plugin: String) =
  mark(createLoggerMark(dataType, plugin))

inline fun Logger.mark(dataType: DatasetType, plugin: String, target: InstallTargetID) =
  mark(createLoggerMark(dataType, plugin) + " $PrefixInstallTarget=$target")

inline fun Logger.mark(ownerID: UserID, datasetID: DatasetID) =
  mark(createLoggerMark(ownerID, datasetID))

inline fun Logger.mark(
  eventID: EventID? = null,
  ownerID: UserID? = null,
  datasetID: DatasetID? = null,
  dataType: DatasetType? = null,
  pluginName: String? = null,
  scriptName: String? = null,
  installTarget: InstallTargetID? = null,
  requestID: String? = null,
) =
  mark(createLoggerMark(eventID, ownerID, datasetID, dataType, pluginName, scriptName, installTarget, requestID))

// endregion Logger Wrapping

// region Generic Logger Builder

inline fun logger(name: String): Logger =
  LoggerFactory.getLogger(name)

inline fun <reified T: Any> markedLogger(
  eventID: EventID? = null,
  ownerID: UserID? = null,
  datasetID: DatasetID? = null,
  dataType: DatasetType? = null,
  pluginName: String? = null,
  scriptName: String? = null,
  installTarget: InstallTargetID? = null,
) = LoggerFactory.getLogger(T::class.java)
  .mark(eventID, ownerID, datasetID, dataType, pluginName, scriptName, installTarget)


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

inline fun Any.logger(): Logger =
  LoggerFactory.getLogger(this::class.java)

inline fun <reified T: Any> logger(): Logger =
  LoggerFactory.getLogger(T::class.java)

// endregion Generic Logger Builder

// region String Building

inline fun createLoggerMark(dataType: DatasetType, pluginName: String) =
  arrayOf("$PrefixDataType=$dataType", "$PrefixPluginName=$pluginName")

inline fun createLoggerMark(ownerID: UserID, datasetID: DatasetID, installTarget: InstallTargetID) =
  arrayOf(
    "$PrefixUserID=$ownerID",
    "$PrefixDatasetID=$datasetID",
    "$PrefixInstallTarget=$installTarget",
  )

inline fun createLoggerMarkString(eventID: EventID, ownerID: UserID, datasetID: DatasetID) =
  arrayOf(
    "$PrefixEventID=$eventID",
    "$PrefixUserID=$ownerID",
    "$PrefixDatasetID=$datasetID",
  ).joinToString(" ")

inline fun createLoggerMark(ownerID: UserID, datasetID: DatasetID) =
  arrayOf("$PrefixUserID=$ownerID", "$PrefixDatasetID=$datasetID")

inline fun createLoggerMark(datasetID: DatasetID, project: InstallTargetID) =
  arrayOf("$PrefixDatasetID=$datasetID", "$PrefixInstallTarget=$project")

fun createLoggerMark(
  eventID: EventID? = null,
  ownerID: UserID? = null,
  datasetID: DatasetID? = null,
  dataType: DatasetType? = null,
  pluginName: String? = null,
  scriptName: String? = null,
  installTarget: InstallTargetID? = null,
  requestID: String? = null,
) = arrayOf(
  eventID?.let { "$PrefixEventID=$it" },
  ownerID?.let { "$PrefixUserID=$it" },
  datasetID?.let { "$PrefixDatasetID=$it" },
  dataType?.let { "$PrefixDataType=$it" },
  pluginName?.let { "$PrefixPluginName=$it" },
  scriptName?.let { "$PrefixScript=$it" },
  installTarget?.let { "$PrefixInstallTarget=$it" },
  requestID?.let { "$PrefixRequestID=$it" },
).let { marks ->
  var last = 0
  Array(marks.count { it != null }) {
    for (i in last ..< marks.size) {
      if (marks[i] != null) {
        last = i+1
        return@Array marks[i]!!
      }
    }

    ""
  }
}

// endregion String Building