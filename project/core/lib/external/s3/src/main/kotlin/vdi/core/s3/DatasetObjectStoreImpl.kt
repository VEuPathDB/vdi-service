package vdi.core.s3

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import vdi.model.data.DatasetID
import vdi.model.data.UserID
import java.util.Spliterator
import java.util.Spliterators
import java.util.stream.Stream
import java.util.stream.StreamSupport
import vdi.core.s3.paths.S3DatasetPathFactory
import vdi.core.s3.paths.S3Paths
import vdi.model.data.toUserIDOrNull

internal class DatasetObjectStoreImpl(private val s3Bucket: S3Bucket) : DatasetObjectStore {
  override fun getDatasetDirectory(ownerID: UserID, datasetID: DatasetID): DatasetDirectory =
    DatasetDirectoryImpl(ownerID, datasetID, s3Bucket, S3DatasetPathFactory(ownerID, datasetID))

  override fun listDatasets(ownerID: UserID): List<DatasetID> =
    s3Bucket.objects.listSubPaths(S3Paths.userDir(ownerID))
      .commonPrefixes()
      .map(::DatasetID)

  override fun streamAllDatasets(): Stream<DatasetDirectory> =
    StreamSupport.stream(
      Spliterators.spliteratorUnknownSize(
      DatasetDirIterator(s3Bucket, s3Bucket.objects.stream().stream().iterator()),
      Spliterator.ORDERED
    ), false)

  override fun listUsers(): List<UserID> =
    s3Bucket.objects.listSubPaths()
      .commonPrefixes()
      .map { it.toUserIDOrNull() ?: throw IllegalStateException("invalid user ID: $it") }
}

