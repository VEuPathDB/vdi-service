package vdi.components.datasets.paths

import vdi.components.common.fields.DatasetID
import vdi.components.common.fields.UserID
import vdi.components.common.fields.toDatasetIDOrNull
import vdi.components.common.fields.toUserIDOrNull


@Suppress("DuplicatedCode")
fun String.toVDPathOrNull(): VDPath? {
  val it = splitToSequence('/')
    .iterator()

  val bucket = it.next()
  if (!it.hasNext() || bucket.isBlank())
    return null

  val root = it.next()
  if (!it.hasNext() || root.isBlank())
    return null

  val userID = it.next().toUserIDOrNull()
  if (!it.hasNext() || userID == null)
    return null

  val datasetID = it.next().toDatasetIDOrNull()
  if (!it.hasNext() || datasetID == null)
    return null

  val segment = it.next()
  if (!it.hasNext() || segment.isBlank())
    return null

  return when (segment) {
    S3Paths.UPLOAD_DIR_NAME  -> toVDUploadPathOrNull(it, bucket, root, userID, datasetID)
    S3Paths.DATASET_DIR_NAME -> toDatasetPathOrNull(it, bucket, root, userID, datasetID)
    else                     -> null
  }
}

private fun toVDUploadPathOrNull(
  it: Iterator<String>,
  bucket: String,
  root: String,
  userID: UserID,
  datasetID: DatasetID,
): VDUploadPath =
  VDUploadPathImpl(bucket, root, userID, datasetID, it.collectToString())

private fun toDatasetPathOrNull(
  it: Iterator<String>,
  bucket: String,
  root: String,
  userID: UserID,
  datasetID: DatasetID,
): VDPath? {
  val seg1 = it.next()
  if (seg1.isBlank())
    return null

  if (!it.hasNext())
    return VDDatasetFilePathImpl(bucket, root, userID, datasetID, seg1)

  return when (seg1) {
    S3Paths.SHARES_DIR_NAME -> toDatasetSharesPathOrNull(it, bucket, root, userID, datasetID)
    S3Paths.DATA_DIR_NAME -> toDatasetDataPathOrNull(it, bucket, root, userID, datasetID)
    else -> null
  }
}

private fun toDatasetSharesPathOrNull(
  it: Iterator<String>,
  bucket: String,
  root: String,
  userID: UserID,
  datasetID: DatasetID,
): VDDatasetShareFilePath? {
  val recipientID = it.next().toUserIDOrNull()
  return if (!it.hasNext() || recipientID == null)
    null
  else
    VDDatasetShareFilePathImpl(bucket, root, userID, datasetID, recipientID, it.collectToString())
}

private fun toDatasetDataPathOrNull(
  it: Iterator<String>,
  bucket: String,
  root: String,
  userID: UserID,
  datasetID: DatasetID,
): VDDatasetDataFilePath =
  VDDatasetDataFilePathImpl(bucket, root, userID, datasetID, it.collectToString())


private inline fun Iterator<String>.collectToString(): String {
  val out = StringBuilder(1024)
    .append(next())

  while (hasNext())
    out.append('/').append(next())

  return out.toString()
}