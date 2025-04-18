@file:Suppress("NOTHING_TO_INLINE")

package vdi.lib.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID

inline val <T: Any> T.logger: Logger
  get() = LoggerFactory.getLogger(this::class.java)

inline fun <T: Any> T.logger(datasetID: DatasetID, ownerID: UserID): Logger =
  DatasetContextLogger(datasetID, ownerID, LoggerFactory.getLogger(this::class.java))

inline fun logger(name: String): Logger =
  LoggerFactory.getLogger(name)

inline fun <reified T: Any> logger(): Logger =
  LoggerFactory.getLogger(T::class.java)

inline fun <reified T: Any> logger(datasetID: DatasetID, ownerID: UserID): Logger =
  DatasetContextLogger(datasetID, ownerID, LoggerFactory.getLogger(T::class.java))
