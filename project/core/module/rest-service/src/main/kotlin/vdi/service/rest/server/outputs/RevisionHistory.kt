package vdi.service.rest.server.outputs

import vdi.model.data.DatasetRevision
import vdi.model.data.DatasetRevisionHistory
import vdi.service.rest.generated.model.DatasetRevisionAction
import vdi.service.rest.generated.model.DatasetRevisionImpl
import vdi.service.rest.generated.model.DatasetRevision as APIRevision
import vdi.service.rest.generated.model.RevisionHistory
import vdi.service.rest.generated.model.RevisionHistoryImpl

internal fun RevisionHistory(revs: DatasetRevisionHistory): RevisionHistory =
  RevisionHistoryImpl().also {
    it.originalId = revs.originalID.toString()
    it.revisions  = revs.revisions.map(::DatasetRevision)
  }

private fun DatasetRevision(rev: DatasetRevision): APIRevision =
  DatasetRevisionImpl().also {
    it.action       = DatasetRevisionAction(rev.action)
    it.fileListUrl  = TODO("make relative path uri")
    it.revisionId   = rev.revisionID.toString()
    it.revisionNote = rev.revisionNote
    it.timestamp    = rev.timestamp
  }

private fun DatasetRevisionAction(action: DatasetRevision.Action) =
  when (action) {
    DatasetRevision.Action.Revise -> DatasetRevisionAction.REVISE
    DatasetRevision.Action.Extend -> throw IllegalStateException()
  }