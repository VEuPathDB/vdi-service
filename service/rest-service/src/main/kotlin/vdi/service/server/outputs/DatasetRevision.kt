package vdi.service.server.outputs

import vdi.service.generated.model.DatasetRevision
import vdi.service.generated.model.DatasetRevisionAction
import vdi.service.generated.model.DatasetRevisionImpl
import vdi.service.util.defaultZone
import org.veupathdb.vdi.lib.common.model.VDIDatasetRevision


/**
 * Creates a new [DatasetRevision] instance from the given internal-form
 * [VDIDatasetRevision] instance, and an optional [revision note][note].
 *
 * The revision note argument is optional as this type may be returned in
 * dataset list endpoints where fetching revision notes for all the result
 * datasets would require making a separate request to the object store for
 * each.
 */
internal fun DatasetRevision(revision: VDIDatasetRevision, note: String?): DatasetRevision =
  DatasetRevisionImpl().apply {
    action       = DatasetRevisionAction.entries.first { it.value == revision.action.stringValue }
    revisionId   = revision.revisionID.toString()
    timestamp    = revision.timestamp.defaultZone()
    revisionNote = note
  }
