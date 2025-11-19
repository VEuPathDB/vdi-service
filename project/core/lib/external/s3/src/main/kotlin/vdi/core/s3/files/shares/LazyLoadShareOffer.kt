package vdi.core.s3.files.shares

import com.fasterxml.jackson.module.kotlin.readValue
import org.veupathdb.lib.s3.s34k.objects.ObjectContainer
import vdi.core.s3.files.LazyDatasetFileImpl
import vdi.json.JSON
import vdi.model.meta.DatasetShareOffer
import vdi.model.meta.UserID

internal class LazyLoadShareOffer(
  override val recipientID: UserID,
  path: String,
  bucket: ObjectContainer,
): LazyDatasetFileImpl(path, bucket), ShareOffer {
  override fun load(): DatasetShareOffer? =
    bucket.open(path)?.stream?.use { JSON.readValue(it) }
}