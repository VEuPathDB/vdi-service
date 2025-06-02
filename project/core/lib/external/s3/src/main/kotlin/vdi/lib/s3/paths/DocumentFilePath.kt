package vdi.lib.s3.paths

import vdi.model.data.DatasetID
import vdi.model.data.UserID
import vdi.lib.s3.util.PathFactory

sealed interface DocumentFilePath: DatasetPath<DocumentFileType> {

  companion object: PathFactory<DocumentFilePath> {
    private val pattern = Regex("^([\\w-]+)/(\\d+)/([\\w.]+)/documents/([^/]+)$")

    override fun matches(path: String) = pattern.matches(path)

    override fun create(path: String): DocumentFilePath =
      pattern.matchEntire(path)!!.destructured.let { (bucket, user, dataset, file) ->
        DocumentFilePathImpl(file, path, UserID(user), DatasetID(dataset), bucket)
      }
  }

  private class DocumentFilePathImpl(
    override val fileName: String,
    override val pathString: String,
    override val userID: UserID,
    override val datasetID: DatasetID,
    override val bucketName: String,
  ): DocumentFilePath {
    override val type = DocumentFileType.Uncategorized
    override fun toString() = pathString
  }
}
