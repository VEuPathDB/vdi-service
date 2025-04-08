package org.veupathdb.service.vdi.genx.model

import org.veupathdb.service.vdi.generated.model.DatasetRevision
import org.veupathdb.service.vdi.generated.model.DatasetRevisionAction
import org.veupathdb.service.vdi.generated.model.DatasetRevisionImpl
import org.veupathdb.service.vdi.util.defaultZone
import org.veupathdb.vdi.lib.common.model.VDIDatasetRevision

internal fun VDIDatasetRevision.toExternal(note: String?): DatasetRevision =
  DatasetRevisionImpl().also {
    it.action = DatasetRevisionAction.entries.first { it.value == action.stringValue }
    it.revisionId = revisionID.toString()
    it.timestamp = timestamp.defaultZone()
    it.revisionNote = note
  }
