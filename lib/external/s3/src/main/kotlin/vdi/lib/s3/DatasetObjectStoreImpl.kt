package vdi.lib.s3

import org.veupathdb.lib.s3.s34k.buckets.S3Bucket
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.field.toUserIDOrNull
import org.veupathdb.vdi.lib.common.util.HostAddress
import java.util.Spliterator
import java.util.Spliterators
import java.util.stream.Stream
import java.util.stream.StreamSupport
import vdi.lib.health.RemoteDependencies
import vdi.lib.s3.paths.S3DatasetPathFactory
import vdi.lib.s3.paths.S3Paths

internal class DatasetObjectStoreImpl(private val s3Bucket: S3Bucket) : DatasetObjectStore {
  private companion object {
    private val knownHosts = HashSet<HostAddress>(1)

    fun init(addr: HostAddress) {
      synchronized(knownHosts) {
        if (addr !in knownHosts) {
          knownHosts.add(addr)
          RemoteDependencies.register("Minio ${addr.host}", addr.host, addr.port)
        }
      }
    }
  }

  override fun getDatasetDirectory(ownerID: UserID, datasetID: DatasetID): DatasetDirectory =
    DatasetDirectoryImpl(ownerID, datasetID, s3Bucket, S3DatasetPathFactory(ownerID, datasetID))

  override fun listDatasets(ownerID: UserID): List<DatasetID> =
    s3Bucket.objects.listSubPaths(S3Paths.userDir(ownerID))
      .commonPrefixes()
      .map(::DatasetID)

  override fun streamAllDatasets(): Stream<DatasetDirectory> =
    StreamSupport.stream(
      Spliterators.spliteratorUnknownSize(
      DatasetDirIterator(s3Bucket.objects.stream().stream().iterator()),
      Spliterator.ORDERED
    ), false)

  override fun listUsers(): List<UserID> =
    s3Bucket.objects.listSubPaths()
      .commonPrefixes()
      .map { it.toUserIDOrNull() ?: throw IllegalStateException("invalid user ID: $it") }
}

