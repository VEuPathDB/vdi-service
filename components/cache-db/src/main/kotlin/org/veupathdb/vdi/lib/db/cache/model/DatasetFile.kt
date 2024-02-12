package org.veupathdb.vdi.lib.db.cache.model

@Deprecated("switch to using common model form")
data class DatasetFile(
  val fileName: String,
  val fileSize: ULong,
)
