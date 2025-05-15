package vdi.lib.s3.paths

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.field.toDatasetIDOrNull
import org.veupathdb.vdi.lib.common.field.toUserIDOrNull

/**
 * Parses an object key from MinIO into a VDI dataset file path.
 *
 * Expected valid input paths:
 *
 * * `{bucket-name}/{user-id}/{dataset-id}/{file-name}`
 * * `{bucket-name}/{user-id}/{dataset-id}/shares/{recipient-id}/{file-name}`
 */
fun String.toDatasetPathOrNull(): DatasetPath? {
  val it = splitToSequence('/')
    .iterator()

  // The first segment in the path should always be the bucket name.
  val bucket = it.next()
  if (!it.hasNext() || bucket.isBlank())
    return null

  // The second segment is the dataset owner's user id
  val userID = it.next().toUserIDOrNull()
  if (!it.hasNext() || userID == null)
    return null

  // The third segment is the id of the dataset
  val datasetID = it.next().toDatasetIDOrNull()
  if (!it.hasNext() || datasetID == null)
    return null

  // The next segment should either be the name of an expected file, or the name
  // of the dataset shares directory.
  val segment = it.next()

  return if (segment == S3File.SharesDir.baseName)
    it.toDatasetSharePathOrNull(bucket, userID, datasetID)
  else
    it.toDatasetFilePathOrNull(bucket, userID, datasetID, segment)
}

private fun Iterator<String>.toDatasetSharePathOrNull(
  bucket: String,
  userID: UserID,
  datasetID: DatasetID,
): DatasetShareFilePath? {
  // If we've gotten here, then the previous segment in the iterator was the
  // shares directory.  The remainder of the iterator should be 2 segments, the
  // recipient user id and the name of the file which must be one of
  // "offer.json" or "receipt.json"

  // Ensure there is another segment in the iterator.
  if (!hasNext())
    return null

  // Ensure the next segment is a valid user ID.
  val recipientID = next().toUserIDOrNull()
  if (!hasNext() || recipientID == null)
    return null

  // Ensure the next segment is one of the valid file names.
  return when (val it = S3File.fromStringOrNull(next())) {
    S3File.ShareOffer,
    S3File.ShareReceipt,
    -> DatasetShareFilePathImpl(bucket, userID, datasetID, recipientID, it)

    else -> null
  }
}

private fun Iterator<String>.toDatasetFilePathOrNull(
  bucket: String,
  userID: UserID,
  datasetID: DatasetID,
  segment: String,
): DatasetFilePath? {
  // If we've gotten here, then we have hit a path segment that is not the
  // shares directory.  At this point the iterator should now contain no more
  // path segments, and the given path segment argument must be one of the known
  // file names.

  // Ensure there are no more segments in the iterator.
  if (hasNext())
    return null

  // Ensure the given path segment is one of the expected file names.
  return S3File.fromStringOrNull(segment)?.let { DatasetFilePathImpl(bucket, userID, datasetID, it) }
}
