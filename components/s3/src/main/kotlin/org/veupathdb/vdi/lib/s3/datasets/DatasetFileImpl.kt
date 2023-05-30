package org.veupathdb.vdi.lib.s3.datasets

import org.slf4j.LoggerFactory
import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.lib.s3.s34k.objects.S3Object
import java.io.InputStream
import java.time.OffsetDateTime

internal sealed class DatasetFileImpl(
  override val name: String,
  override val path: String,
  private val existsChecker: () -> Boolean,
  private val lastModifiedSupplier: () -> OffsetDateTime?,
  private val loadObjectStream: () -> InputStream?
) : DatasetFile {
  private val log = LoggerFactory.getLogger(javaClass)

  override fun exists() = existsChecker.invoke()
  override fun lastModified(): OffsetDateTime? {
    val lastModified = lastModifiedSupplier.invoke()
    log.info("Last modified time for {}: {}", name, lastModified)
    return lastModified
  }
  override fun loadContents() = loadObjectStream.invoke()

  constructor(s3Object: S3Object) : this(
    name = s3Object.baseName,
    path = s3Object.path,
    lastModifiedSupplier = { s3Object.lastModified },
    existsChecker = { true }, // It definitely exists if loaded from an actual S3 object
    loadObjectStream = { s3Object.bucket.objects.open(s3Object.path)?.stream }
  )

  constructor(
    name: String,
    bucket: S3Bucket,
    path: String,
  ) : this(
    name = name,
    path = path,
    lastModifiedSupplier = { bucket.objects.list(path).stream().findAny().get().lastModified }, // Wow! This isn't great
    existsChecker = { path in bucket.objects },
    loadObjectStream = { bucket.objects.open(path)?.stream }
  )
}