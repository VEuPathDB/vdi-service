package vdi.core.s3.paths

import vdi.core.s3.files.FileName
import vdi.core.s3.files.ShareFileType
import vdi.model.data.DatasetID
import vdi.model.data.UserID
import vdi.core.s3.util.PathFactory

sealed interface ShareFilePath: DatasetPath<ShareFileType> {
  val recipientID: UserID

  companion object: PathFactory<ShareFilePath> {
    private val pattern = Regex("^([\\w-]+)/(\\d+)/([\\w.]+)/shares/(\\d+)/([^/]+)$")

    override fun matches(path: String) = pattern.matches(path)
      && path.substringAfterLast('/').let { name -> ShareFileType.entries.any { it.fileName == name } }

    override fun create(path: String): ShareFilePath =
      pattern.matchEntire(path)!!.destructured.let { (bucket, user, dataset, recipient, file) ->
        ShareFilePathImpl(file, UserID(user), DatasetID(dataset), UserID(recipient), bucket)
      }
  }

  private class ShareFilePathImpl(
    fileName: String,
    override val userID: UserID,
    override val datasetID: DatasetID,
    override val recipientID: UserID,
    override val bucketName: String,
  ) : ShareFilePath {
    override val type = ShareFileType.entries.firstOrNull { it.fileName == fileName }
      ?: throw IllegalArgumentException("unrecognized dataset path: $pathString")
    override val fileName: String
      get() = type.fileName
    override val pathString: String
      get() = "$bucketName/$userID/$datasetID/${FileName.ShareDirectoryName}/$recipientID/$fileName"

    override fun toString() = pathString
  }
}

