package org.veupathdb.vdi.lib.s3.datasets

import com.fasterxml.jackson.module.kotlin.readValue
import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.lib.s3.s34k.objects.S3Object
import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import org.veupathdb.vdi.lib.json.JSON
import org.veupathdb.vdi.lib.s3.datasets.paths.S3Paths
import java.io.InputStream
import java.time.OffsetDateTime

internal class DatasetMetaFileImpl(override val name: String,
                                   path: String,
                                   existsChecker: () -> Boolean,
                                   lastModifiedSupplier: () -> OffsetDateTime?,
                                   loadObjectStream: () -> InputStream?)
  : DatasetFileImpl(name, path, existsChecker, lastModifiedSupplier, loadObjectStream)
  , DatasetMetaFile
{
  /**
   * Lazily initialize, without knowledge of object's existence.
   */
  constructor(
    bucket: S3Bucket,
    path: String,
  ): this(
    name = S3Paths.META_FILE_NAME,
    path = path,
    lastModifiedSupplier = { bucket.objects.list(path).stream().findAny().get().lastModified }, // Wow! This isn't great
    existsChecker = { path in bucket.objects },
    loadObjectStream = { bucket.objects.open(path)?.stream }
  )

  /**
   * Eagerly initialize, with assurance of object's existence
   */
  constructor(s3Object: S3Object): this(
    name = S3Paths.META_FILE_NAME,
    path = s3Object.path,
    lastModifiedSupplier = { s3Object.lastModified },
    existsChecker = { true }, // It definitely exists if loaded from an actual S3 object
    loadObjectStream = { s3Object.bucket.objects.open(s3Object.path)?.stream }
  ) {
    if (s3Object.baseName != S3Paths.META_FILE_NAME) {
      throw IllegalArgumentException("Can only construct a MetaFile from s3 object if object base name is "
              + S3Paths.META_FILE_NAME)
    }
  }

  override fun load(): VDIDatasetMeta? = loadContents()?.use { JSON.readValue(it) }
}