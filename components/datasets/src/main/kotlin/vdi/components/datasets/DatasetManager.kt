package vdi.components.datasets

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import vdi.components.common.DatasetID

class DatasetManager(private val s3Bucket: S3Bucket) {
  fun getDatasetDirectory(ownerID: Long, datasetID: DatasetID): DatasetDirectory {
    return DatasetDirectoryImpl(ownerID, datasetID, s3Bucket)
  }
}