package vdi.service.rest.server.outputs

import vdi.model.meta.DatasetRevision
import vdi.model.meta.DatasetRevisionHistory
import vdi.service.rest.generated.model.DatasetRevisionAction
import vdi.service.rest.generated.model.DatasetRevisionImpl
import vdi.service.rest.generated.model.RevisionHistory
import vdi.service.rest.generated.model.RevisionHistoryImpl
import vdi.service.rest.generated.model.DatasetRevision as APIRevision

internal fun RevisionHistory(revs: DatasetRevisionHistory): RevisionHistory =
  RevisionHistoryImpl().also { response ->
    response.originalId = revs.originalID.toString()
    response.revisions  = revs.revisions.map(::DatasetRevision)
  }

private fun DatasetRevision(rev: DatasetRevision): APIRevision =
  DatasetRevisionImpl().also {
    it.action       = DatasetRevisionAction(rev.action)
    it.revisionId   = rev.revisionID.toString()
    it.revisionNote = rev.revisionNote
    it.timestamp    = rev.timestamp
  }

private fun DatasetRevisionAction(action: DatasetRevision.Action) =
  when (action) {
    DatasetRevision.Action.Revise -> DatasetRevisionAction.REVISE
    DatasetRevision.Action.Create -> DatasetRevisionAction.CREATE
    DatasetRevision.Action.Extend -> DatasetRevisionAction.EXTEND
  }