package vdi.components.datasets

import com.fasterxml.jackson.module.kotlin.readValue
import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import vdi.components.datasets.model.DatasetMeta
import vdi.components.json.JSON

internal class DatasetMetaFileImpl(bucket: S3Bucket, path: String) : DatasetFileImpl(bucket, path), DatasetMetaFile {
  override fun load(): DatasetMeta? = bucket.objects.open(path)?.stream?.use { JSON.readValue(it) }
}