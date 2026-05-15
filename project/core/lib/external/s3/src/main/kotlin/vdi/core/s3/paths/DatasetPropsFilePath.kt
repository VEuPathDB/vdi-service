package vdi.core.s3.paths

import vdi.core.s3.files.FileName
import vdi.core.s3.util.PathFactory
import vdi.model.meta.DatasetID
import vdi.model.meta.UserID

sealed interface DatasetPropsFilePath: DatasetPath {

  companion object: PathFactory<DatasetPropsFilePath> {
    private val pattern = Regex("^([\\w-]+)/(\\d+)/([\\w.]+)/${FileName.DatasetPropsDirectory}/([^/]+)$")

    override fun matches(path: String) = pattern.matches(path)

    override fun create(path: String): DatasetPropsFilePath =
      pattern.matchEntire(path)!!.destructured.let { (bucket, user, dataset, file) ->
        DatasetPropsFilePathImpl(file, path, UserID(user), DatasetID(dataset), bucket) }
  }

  private class DatasetPropsFilePathImpl(
    override val fileName: String,
    override val pathString: String,
    override val userID: UserID,
    override val datasetID: DatasetID,
    override val bucketName: String
  ): DatasetPropsFilePath
}