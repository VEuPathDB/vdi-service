package vdi.core.s3.paths

import vdi.core.s3.files.FileName
import vdi.model.meta.DatasetID
import vdi.model.meta.UserID
import vdi.core.s3.util.PathFactory

/**
 * VDI-internal metadata file path.
 *
 * These files are stored at the root of the dataset directory in the object
 * store.
 */
sealed interface MetaFilePath: DatasetPath {

  companion object: PathFactory<MetaFilePath> {
    private val pattern = Regex("^([\\w-]+)/(\\d+)/([\\w.]+)/([^/]+)$")

    override fun matches(path: String) = pattern.matches(path)
      && path.substringAfterLast('/')
        .let { FileName.MetaFileNames.any(it::equals) }

    override fun create(path: String): MetaFilePath =
      pattern.matchEntire(path)!!.destructured.let { (bucket, user, dataset, file) ->
        MetaFilePathImpl(file, UserID(user), DatasetID(dataset), bucket) }
  }

  private class MetaFilePathImpl(
    override val fileName: String,
    override val userID: UserID,
    override val datasetID: DatasetID,
    override val bucketName: String
  ): MetaFilePath {
    override val pathString: String
      get() = "$bucketName/$userID/$datasetID/$fileName"

    override fun toString() = pathString
  }
}