package org.veupathdb.vdi.lib.s3.datasets

import com.fasterxml.jackson.module.kotlin.readValue
import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.lib.s3.s34k.objects.S3Object
import org.veupathdb.vdi.lib.common.DatasetManifestFilename
import org.veupathdb.vdi.lib.common.model.VDIDatasetManifest
import org.veupathdb.vdi.lib.json.JSON
import java.io.InputStream
import java.time.OffsetDateTime

internal class DatasetManifestFileImpl(path: String,
                                       existsChecker: () -> Boolean,
                                       lastModifiedSupplier: () -> OffsetDateTime?,
                                       loadObjectStream: () -> InputStream?)
: DatasetFileImpl(DatasetManifestFilename, path, existsChecker, lastModifiedSupplier, loadObjectStream)
, DatasetManifestFile
{
  constructor(
    bucket: S3Bucket,
    path: String,
  ): this(
    path = path,
    // This looks weird, but we use list instead of stat since stat only returns seconds resolution, not milliseconds.
    lastModifiedSupplier = { bucket.objects.list(path).stream().findFirst().map { o -> o.lastModified }.orElse(null) },
    existsChecker = { path in bucket.objects },
    loadObjectStream = { bucket.objects.open(path)?.stream }
  )

  constructor(s3Object: S3Object): this(
    path = s3Object.path,
    lastModifiedSupplier = { s3Object.lastModified },
    existsChecker = { true }, // It definitely exists if loaded from an actual S3 object
    loadObjectStream = { s3Object.bucket.objects.open(s3Object.path)?.stream }
  ) {
    if (s3Object.baseName != DatasetManifestFilename) {
      throw IllegalArgumentException("Can only construct a MetaFile from s3 object if object base name is "
              + DatasetManifestFilename)
    }
  }

  override fun load(): VDIDatasetManifest? = loadContents()?.use { JSON.readValue(it) }
}