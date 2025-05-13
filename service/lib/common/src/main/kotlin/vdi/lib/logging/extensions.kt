@file:Suppress("NOTHING_TO_INLINE")
package vdi.lib.logging

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID

inline fun Any.logger(): Logger =
  LoggerFactory.getLogger(this::class.java)

inline fun logger(name: String): Logger =
  LoggerFactory.getLogger(name)

inline fun <reified T: Any> logger(): Logger =
  LoggerFactory.getLogger(T::class.java)

inline fun Any.markedLogger(mark: String): Logger =
  MarkedLogger(mark, javaClass)

inline fun Any.markedLogger(ownerID: UserID, datasetID: DatasetID): Logger =
  markedLogger("D=$ownerID/$datasetID")

inline fun <reified T: Any> markedLogger(mark: String): Logger =
  MarkedLogger(mark, T::class)

inline fun <reified T: Any> markedLogger(ownerID: UserID, datasetID: DatasetID): Logger =
  markedLogger<T>("D=$ownerID/$datasetID")
