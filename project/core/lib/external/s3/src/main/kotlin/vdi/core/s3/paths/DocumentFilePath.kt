package vdi.core.s3.paths

import vdi.core.s3.files.FileName
import vdi.model.meta.DatasetID
import vdi.model.meta.UserID
import vdi.core.s3.util.PathFactory

sealed interface DocumentFilePath: DatasetPath {

  companion object: PathFactory<DocumentFilePath> {
    private val pattern = Regex("^([\\w-]+)/(\\d+)/([\\w.]+)/${FileName.DocumentDirectory}/([^/]+)$")

    override fun matches(path: String) = pattern.matches(path)

    override fun create(path: String): DocumentFilePath =
      pattern.matchEntire(path)!!.destructured.let { (bucket, user, dataset, file) ->
        DocumentFilePathImpl(file, path, UserID(user), DatasetID(dataset), bucket) }
  }

  private class DocumentFilePathImpl(
    override val fileName: String,
    override val pathString: String,
    override val userID: UserID,
    override val datasetID: DatasetID,
    override val bucketName: String,
  ): DocumentFilePath {
    override fun toString() = pathString
  }
}
