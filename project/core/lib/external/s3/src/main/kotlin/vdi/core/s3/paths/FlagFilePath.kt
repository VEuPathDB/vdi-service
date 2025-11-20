package vdi.core.s3.paths

import vdi.core.s3.files.FileName
import vdi.model.meta.DatasetID
import vdi.model.meta.UserID
import vdi.core.s3.util.PathFactory

sealed interface FlagFilePath: DatasetPath {

  companion object: PathFactory<FlagFilePath> {
    private val pattern = Regex("^([\\w-]+)/(\\d+)/([\\w.]+)/([^/]+)$")

    override fun matches(path: String) = pattern.matches(path)
      && path.substringAfterLast('/')
        .let { FileName.FlagFileNames.any(it::equals) }

    override fun create(path: String): FlagFilePath =
      pattern.matchEntire(path)!!.destructured.let { (bucket, user, dataset, file) ->
        FlagFilePathImpl(file, UserID(user), DatasetID(dataset), bucket) }
  }

  private class FlagFilePathImpl(
    override val fileName: String,
    override val userID: UserID,
    override val datasetID: DatasetID,
    override val bucketName: String,
  ): FlagFilePath {
    override val pathString: String
      get() = "$bucketName/$userID/$datasetID/$fileName"

    override fun toString() = pathString
  }
}
