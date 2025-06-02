package vdi.core.s3.paths

private val pathFactories = arrayOf(
  MetaFilePath.Companion,
  DataFilePath.Companion,
  DocumentFilePath.Companion,
  ShareFilePath.Companion,
  FlagFilePath.Companion,
)

/**
 * Parses an object key from MinIO into a VDI dataset file path.
 *
 * Expected valid input paths:
 *
 * * `{bucket-name}/{user-id}/{dataset-id}/{file-name}`
 * * `{bucket-name}/{user-id}/{dataset-id}/documents/{file-name}`
 * * `{bucket-name}/{user-id}/{dataset-id}/shares/{recipient-id}/{file-name}`
 */
fun String.toDatasetPathOrNull(): DatasetPath<*>? {
  return pathFactories.firstNotNullOfOrNull {
    if (it.matches(this))
      it.create(this)
    else
      null
  }
}
