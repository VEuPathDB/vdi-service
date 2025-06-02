package vdi.lib.s3.paths

import vdi.model.data.DatasetID
import vdi.model.data.UserID
import java.lang.IllegalArgumentException
import vdi.lib.s3.files.DataFileType
import vdi.lib.s3.util.PathFactory

sealed interface DataFilePath: DatasetPath<DataFileType> {

  companion object: PathFactory<DataFilePath> {
    private val pattern = Regex("^([\\w-]+)/(\\d+)/([\\w.]+)/([^/]+)$")

    override fun matches(path: String) = pattern.matches(path)
      && path.substringAfterLast('/').let { name -> DataFileType.entries.any { it.fileName == name } }

    override fun create(path: String): DataFilePath =
      pattern.matchEntire(path)!!.destructured.let { (bucket, user, dataset, file) ->
        DataFilePathImpl(file, UserID(user), DatasetID(dataset), bucket)
      }
  }

  private class DataFilePathImpl(
    fileName: String,
    override val userID: UserID,
    override val datasetID: DatasetID,
    override val bucketName: String,
  ) : DataFilePath {
    override val type = DataFileType.entries.firstOrNull { it.fileName == fileName }
      ?: throw IllegalArgumentException("unrecognized dataset path: $pathString")
    override val fileName: String
      get() = type.fileName
    override val pathString: String
      get() = "$bucketName/$userID/$datasetID/$fileName"

    override fun toString() = pathString
  }
}
