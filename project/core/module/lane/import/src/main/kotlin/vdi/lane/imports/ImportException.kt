package vdi.lane.imports

import vdi.model.meta.UserID

class ImportException(
  val ownerId: UserID,
  val datasetId: String,
  message: String,
  cause: Throwable?,
): Throwable(message, cause) {
  constructor(ownerId: UserID, datasetId: String): this(
    ownerId,
    datasetId,
    "import failed for dataset $ownerId/$datasetId",
    null,
  )

  constructor(ownerId: UserID, datasetId: String, message: String): this(
    ownerId,
    datasetId,
    message,
    null
  )

  constructor(ownerId: UserID, datasetId: String, cause: Throwable): this(
    ownerId,
    datasetId,
    "import failed for dataset $ownerId/$datasetId",
    cause,
  )

  fun log(logFn: (String, Throwable) -> Unit) {
    logFn(message!!, this)
  }
}