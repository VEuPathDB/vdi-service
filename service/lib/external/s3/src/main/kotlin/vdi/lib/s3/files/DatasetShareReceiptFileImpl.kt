package vdi.lib.s3.files

import com.fasterxml.jackson.module.kotlin.readValue
import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.lib.s3.s34k.objects.S3Object
import org.veupathdb.vdi.lib.common.model.VDIDatasetShareReceipt
import org.veupathdb.vdi.lib.json.JSON
import java.io.InputStream
import java.time.OffsetDateTime
import vdi.lib.s3.paths.S3File

internal class DatasetShareReceiptFileImpl(
  path: String,
  existsChecker: () -> Boolean = { false },
  lastModifiedSupplier: () -> OffsetDateTime? = { null },
  loadObjectStream: () -> InputStream? = { null },
  putObjectStream: (InputStream) -> Unit = { it.skip(Long.MAX_VALUE) },
)
  : DatasetFileImpl(path, existsChecker, lastModifiedSupplier, loadObjectStream, putObjectStream)
  , DatasetShareReceiptFile
{
  /**
   * Lazily initialize, without knowledge of object's existence.
   */
  constructor(bucket: S3Bucket, path: String) : this(
    path = path,
    // This looks weird, but we use list instead of stat since stat only returns seconds resolution, not milliseconds.
    lastModifiedSupplier = { bucket.objects.list(path).stream().findFirst().map { o -> o.lastModified }.orElse(null) },
    existsChecker = { path in bucket.objects },
    loadObjectStream = { bucket.objects.open(path)?.stream },
    putObjectStream = { bucket.objects.put(path) {
      contentType = "application/json"
      stream = it
    } },
  )

  /**
   * Eagerly initialize, with assurance of object's existence
   */
  constructor(s3Object: S3Object) : this(
    path = s3Object.path,
    lastModifiedSupplier = s3Object::lastModified,
    existsChecker = { true }, // It definitely exists if loaded from an actual S3 object
    loadObjectStream = { s3Object.bucket.objects.open(s3Object.path)?.stream }
  ) {
    if (!S3File.ShareReceipt.resembles(s3Object.baseName)) {
      throw IllegalArgumentException(
        "Can only construct a share receipt from s3 object if object base name is "
          + S3File.ShareReceipt + ". Given path: " + s3Object.path
      )
    }
  }

  override fun load(): VDIDatasetShareReceipt? = loadContents()?.use { JSON.readValue(it) }
}
