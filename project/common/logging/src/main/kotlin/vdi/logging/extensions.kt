@file:Suppress("NOTHING_TO_INLINE")
package vdi.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import vdi.model.data.DatasetID
import vdi.model.data.InstallTargetID
import vdi.model.data.UserID

inline fun Any.logger(): Logger =
  LoggerFactory.getLogger(this::class.java)

inline fun logger(name: String): Logger =
  LoggerFactory.getLogger(name)

inline fun <reified T: Any> logger(): Logger =
  LoggerFactory.getLogger(T::class.java)

inline fun Any.markedLogger(mark: String): Logger =
  MarkedLogger(mark, javaClass)

inline fun createLoggerMark(ownerID: UserID, datasetID: DatasetID) =
  "D=$ownerID/$datasetID"

inline fun Any.markedLogger(ownerID: UserID, datasetID: DatasetID): Logger =
  markedLogger(createLoggerMark(ownerID, datasetID))

inline fun createLoggerMark(ownerID: UserID, datasetID: DatasetID, project: InstallTargetID) =
  "D=$ownerID/$datasetID P=$project"

inline fun Any.markedLogger(ownerID: UserID, datasetID: DatasetID, project: InstallTargetID): Logger =
  markedLogger(createLoggerMark(ownerID, datasetID, project))

inline fun createLoggerMark(datasetID: DatasetID, project: InstallTargetID) =
  "D=$datasetID P=$project"

inline fun Any.markedLogger(datasetID: DatasetID, project: InstallTargetID): Logger =
  markedLogger(createLoggerMark(datasetID, project))

inline fun <reified T: Any> markedLogger(mark: String): Logger =
  MarkedLogger(mark, T::class)

inline fun <reified T: Any> markedLogger(ownerID: UserID, datasetID: DatasetID): Logger =
  markedLogger<T>("D=$ownerID/$datasetID")
