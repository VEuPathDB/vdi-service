package vdi.service.rest.server.outputs

import vdi.model.data.DatasetRevision
import vdi.service.rest.generated.model.DatasetRevision as APIRevision
import vdi.service.rest.generated.model.DatasetRevisionImpl
import vdi.service.rest.util.defaultZone


/**
 * Creates a new [DatasetRevision] instance from the given internal-form
 * [DatasetRevision] instance, and an optional [revision note][note].
 *
 * The revision note argument is optional as this type may be returned in
 * dataset list endpoints where fetching revision notes for all the result
 * datasets would require making a separate request to the object store for
 * each.
 */
internal fun DatasetRevision(revision: DatasetRevision, note: String?): APIRevision =
  DatasetRevisionImpl().apply {
    action       = vdi.service.rest.generated.model.DatasetRevisionAction.entries.first { it.value == revision.action.stringValue }
    revisionId   = revision.revisionID.toString()
    timestamp    = revision.timestamp.defaultZone()
    revisionNote = note
  }
