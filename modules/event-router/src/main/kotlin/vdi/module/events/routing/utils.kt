package vdi.module.events.routing

import vdi.components.common.fields.DatasetID
import vdi.components.datasets.paths.S3Paths
import vdi.module.events.routing.model.MinIOEvent

@OptIn(ExperimentalStdlibApi::class)
internal fun MinIOEvent.isDatasetEvent(bucketName: String): Boolean {
  val segments = objectKey.split('/')

  if (segments.size < 5)
    return false

  // If the first path segment does not match our configured bucket name
  if (segments[0] != bucketName)
    return false

  // If the second path segment does not match our expected root dir name
  if (segments[1] != S3Paths.ROOT_DIR_NAME)
    return false

  // If the third path segment (user id) is blank
  if (segments[2].isBlank())
    return false

  try {
    DatasetID(segments[3])
  } catch (e: Throwable) {
    return false
  }

  if (!(segments[4] == S3Paths.DATASET_DIR_NAME || segments[4] == S3Paths.UPLOAD_DIR_NAME))
    return false

  for (i in 5 ..< segments.size)
    if (segments[i].isBlank())
      return false

  return true
}

internal data class DatasetPathSplit(
  val userID: String,
  val datasetID: String,
  val segment: String,
  val remainder: String,
)

internal fun String.splitDatasetPath(): DatasetPathSplit {
  // bucket/root/user-id/dataset-id/...
  val bucketNameEnd = indexOf('/')
  val rootNameEnd   = indexOf('/', bucketNameEnd + 1)
  val userIDEnd     = indexOf('/', rootNameEnd + 1)
  val datasetIDEnd  = indexOf('/', userIDEnd + 1)
  val segmentEnd    = indexOf('/', datasetIDEnd + 1)

  return DatasetPathSplit(
    substring(rootNameEnd + 1, userIDEnd),
    substring(userIDEnd + 1, datasetIDEnd),
    substring(datasetIDEnd + 1, segmentEnd),
    substring(segmentEnd + 1)
  )
}

internal fun String.isSharePath(): Boolean {
  val dirEnd = indexOf('/')

  return if (dirEnd == -1)
    false
  else
    substring(dirEnd) == S3Paths.SHARES_DIR_NAME
}

internal data class SharePathSplit(
  val recipientID: String,
  val fileName: String,
)

internal fun String.splitSharePath(): SharePathSplit {
  val recipientStart = indexOf('/') + 1
  val recipientEnd   = indexOf('/', recipientStart)
  val fileStart      = recipientEnd + 1

  return SharePathSplit(substring(recipientStart, recipientEnd), substring(fileStart))
}