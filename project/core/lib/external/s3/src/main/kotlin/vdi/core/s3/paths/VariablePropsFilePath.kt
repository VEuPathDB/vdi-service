package vdi.core.s3.paths

import vdi.core.s3.files.FileName
import vdi.core.s3.util.PathFactory
import vdi.model.meta.DatasetID
import vdi.model.meta.UserID

sealed interface VariablePropsFilePath: DatasetPath {

  companion object: PathFactory<VariablePropsFilePath> {
    private val pattern = Regex("^([\\w-]+)/(\\d+)/([\\w.]+)/${FileName.MappingDirectory}/([^/]+)$")

    override fun matches(path: String) = pattern.matches(path)

    override fun create(path: String): VariablePropsFilePath =
      pattern.matchEntire(path)!!.destructured.let { (bucket, user, dataset, file) ->
        VariablePropsFilePathImpl(file, path, UserID(user), DatasetID(dataset), bucket) }
  }

  private class VariablePropsFilePathImpl(
    override val fileName: String,
    override val pathString: String,
    override val userID: UserID,
    override val datasetID: DatasetID,
    override val bucketName: String
  ): VariablePropsFilePath
}