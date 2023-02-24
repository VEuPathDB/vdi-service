package vdi.components.datasets

import com.fasterxml.jackson.module.kotlin.readValue
import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import vdi.components.datasets.model.DatasetManifest
import vdi.components.json.JSON

internal class DatasetManifestFileImpl(bucket: S3Bucket, path: String)
: DatasetFileImpl(bucket, path)
, DatasetManifestFile
{
  override fun load(): DatasetManifest? = bucket.objects.open(path)?.stream?.use { JSON.readValue(it) }
}