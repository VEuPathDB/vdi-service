package org.veupathdb.service.vdi.generated.model

import org.veupathdb.vdi.lib.db.cache.model.DatasetFile

fun FileSummary(file: DatasetFile): FileSummary =
  FileSummaryImpl().also { it.name = file.fileName; it.size = file.fileSize.toLong() }