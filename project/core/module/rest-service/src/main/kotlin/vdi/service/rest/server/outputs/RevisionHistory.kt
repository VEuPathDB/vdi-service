package vdi.service.rest.server.outputs

import java.net.URI
import vdi.model.data.DatasetRevision
import vdi.model.data.DatasetRevisionHistory
import vdi.service.rest.generated.model.DatasetRevisionAction
import vdi.service.rest.generated.model.DatasetRevisionImpl
import vdi.service.rest.generated.model.DatasetRevision as APIRevision
import vdi.service.rest.generated.model.RevisionHistory
import vdi.service.rest.generated.model.RevisionHistoryImpl

internal fun RevisionHistory(revs: DatasetRevisionHistory, fileList: URI): RevisionHistory =
  RevisionHistoryImpl().also { response ->
    response.originalId = revs.originalID.toString()
    response.revisions  = revs.revisions.map { DatasetRevision(it, fileList) }
  }

private fun DatasetRevision(rev: DatasetRevision, fileList: URI): APIRevision =
  DatasetRevisionImpl().also {
    it.action       = DatasetRevisionAction(rev.action)
    it.fileListUrl  = fileList.toString()
    it.revisionId   = rev.revisionID.toString()
    it.revisionNote = rev.revisionNote
    it.timestamp    = rev.timestamp
  }

private fun DatasetRevisionAction(action: DatasetRevision.Action) =
  when (action) {
    DatasetRevision.Action.Revise -> DatasetRevisionAction.REVISE
    DatasetRevision.Action.Extend -> throw IllegalStateException()
  }