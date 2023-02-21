package vdi.components.datasets

import com.fasterxml.jackson.module.kotlin.readValue
import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import vdi.components.datasets.model.DatasetShareReceipt
import vdi.components.json.JSON

internal class DatasetShareReceiptFileImpl(bucket: S3Bucket, path: String)
: DatasetFileImpl(bucket, path)
, DatasetShareReceiptFile
{
  override fun load(): DatasetShareReceipt? = bucket.objects.open(path)?.stream?.use { JSON.readValue(it) }
}