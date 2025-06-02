package vdi.lib.s3.paths

import vdi.model.data.DatasetID
import vdi.model.data.UserID
import java.lang.IllegalArgumentException
import vdi.lib.s3.files.FlagFileType
import vdi.lib.s3.util.PathFactory

sealed interface FlagFilePath: DatasetPath<FlagFileType> {

  companion object: PathFactory<FlagFilePath> {
    private val pattern = Regex("^([\\w-]+)/(\\d+)/([\\w.]+)/([^/]+)$")

    override fun matches(path: String) = pattern.matches(path)
      && path.substringAfterLast('/').let { name -> FlagFileType.entries.any { it.fileName == name } }

    override fun create(path: String): FlagFilePath =
      pattern.matchEntire(path)!!.destructured.let { (bucket, user, dataset, file) ->
        FlagFilePathImpl(file, UserID(user), DatasetID(dataset), bucket)
      }
  }

  private class FlagFilePathImpl(
    fileName: String,
    override val userID: UserID,
    override val datasetID: DatasetID,
    override val bucketName: String,
  ): FlagFilePath {
    override val type = FlagFileType.entries.firstOrNull { it.fileName == fileName }
      ?: throw IllegalArgumentException("unrecognized dataset path: $pathString")

    override val fileName: String
      get() = type.fileName

    override val pathString: String
      get() = "$bucketName/$userID/$datasetID/$fileName"

    override fun toString() = pathString
  }
}
