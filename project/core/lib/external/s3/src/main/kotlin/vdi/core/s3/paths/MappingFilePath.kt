package vdi.core.s3.paths

import vdi.core.s3.files.FileName
import vdi.core.s3.util.PathFactory
import vdi.model.meta.DatasetID
import vdi.model.meta.UserID

sealed interface MappingFilePath: DatasetPath {

  companion object: PathFactory<MappingFilePath> {
    private val pattern = Regex("^([\\w-]+)/(\\d+)/([\\w.]+)/${FileName.MappingDirectory}/([^/]+)$")

    override fun matches(path: String) = pattern.matches(path)

    override fun create(path: String): MappingFilePath =
      pattern.matchEntire(path)!!.destructured.let { (bucket, user, dataset, file) ->
        MappingFilePathImpl(file, path, UserID(user), DatasetID(dataset), bucket) }
  }

  private class MappingFilePathImpl(
    override val fileName: String,
    override val pathString: String,
    override val userID: UserID,
    override val datasetID: DatasetID,
    override val bucketName: String
  ): MappingFilePath
}