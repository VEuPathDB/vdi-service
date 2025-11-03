package vdi.core.s3.paths

import vdi.core.s3.files.MetaFileType
import vdi.model.data.DatasetID
import vdi.model.data.UserID
import vdi.core.s3.util.PathFactory

sealed interface MetaFilePath: DatasetPath<MetaFileType> {

  companion object: PathFactory<MetaFilePath> {
    private val pattern = Regex("^([\\w-]+)/(\\d+)/([\\w.]+)/([^/]+)$")

    override fun matches(path: String) = pattern.matches(path)
      && path.substringAfterLast('/').let { name -> MetaFileType.entries.any { it.fileName == name } }

    override fun create(path: String): MetaFilePath =
      pattern.matchEntire(path)!!.destructured.let { (bucket, user, dataset, file) ->
        MetaFilePathImpl(file, UserID(user), DatasetID(dataset), bucket)
      }
  }

  private class MetaFilePathImpl(
    fileName: String,
    override val userID: UserID,
    override val datasetID: DatasetID,
    override val bucketName: String
  ): MetaFilePath {
    override val type = MetaFileType.entries.firstOrNull { it.fileName == fileName }
      ?: throw IllegalArgumentException("unrecognized dataset path: $pathString")
    override val fileName: String
      get() = type.fileName
    override val pathString: String
      get() = "$bucketName/$userID/$datasetID/$fileName"

    override fun toString() = pathString
  }
}