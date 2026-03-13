package vdi.lane.meta

import vdi.model.meta.UserID

class UpdateMetaException(
  val ownerId: UserID,
  val datasetId: String,
  message: String,
  cause: Throwable?,
): Throwable(message, cause) {
  constructor(ownerId: UserID, datasetId: String, cause: Throwable): this(
    ownerId,
    datasetId,
    "install-meta failed for dataset $ownerId/$datasetId",
    cause,
  )

  fun log(logFn: (String, Throwable) -> Unit) {
    logFn(message!!, this)
  }
}
