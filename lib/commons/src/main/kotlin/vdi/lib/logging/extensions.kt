package vdi.lib.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID

inline fun <reified T: Any> T.logger(): Logger =
  LoggerFactory.getLogger(T::class.java)

inline fun <reified T: Any> T.logger(datasetID: DatasetID, ownerID: UserID): Logger =
  DatasetContextLogger(datasetID, ownerID, LoggerFactory.getLogger(T::class.java))

inline fun <reified T: Any> logger(datasetID: DatasetID, ownerID: UserID): Logger =
  DatasetContextLogger(datasetID, ownerID, LoggerFactory.getLogger(T::class.java))
