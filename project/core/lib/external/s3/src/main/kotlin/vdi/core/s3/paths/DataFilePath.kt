package vdi.core.s3.paths

import vdi.model.meta.DatasetID
import vdi.model.meta.UserID
import vdi.core.s3.files.FileName
import vdi.core.s3.util.PathFactory

sealed interface DataFilePath: DatasetPath{
  companion object: PathFactory<DataFilePath> {
    private val pattern = Regex("^([\\w-]+)/(\\d+)/([\\w.]+)/([^/]+)$")

    override fun matches(path: String) = pattern.matches(path)
      && path.substringAfterLast('/')
        .let { name -> FileName.DataFileNames.any(name::equals) }

    override fun create(path: String): DataFilePath =
      pattern.matchEntire(path)!!.destructured.let { (bucket, user, dataset, file) ->
        DataFilePathImpl(file, UserID(user), DatasetID(dataset), bucket) }
  }

  private class DataFilePathImpl(
    override val fileName: String,
    override val userID: UserID,
    override val datasetID: DatasetID,
    override val bucketName: String,
  ): DataFilePath {
    override val pathString: String
      get() = "$bucketName/$userID/$datasetID/$fileName"

    override fun toString() = pathString
  }
}
