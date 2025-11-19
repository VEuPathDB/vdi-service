package vdi.core.s3.paths

import vdi.core.s3.files.FileName
import vdi.model.meta.DatasetID
import vdi.model.meta.UserID
import vdi.core.s3.util.PathFactory

sealed interface ShareFilePath: DatasetPath{
  val recipientID: UserID

  companion object: PathFactory<ShareFilePath> {
    private val pattern = Regex("^([\\w-]+)/(\\d+)/([\\w.]+)/${FileName.ShareDirectory}/(\\d+)/([^/]+)$")

    override fun matches(path: String) = pattern.matches(path)
      && path.substringAfterLast('/')
        .let { it == FileName.ShareOfferFile || it == FileName.ShareReceiptFile  }

    override fun create(path: String): ShareFilePath =
      pattern.matchEntire(path)!!.destructured.let { (bucket, user, dataset, recipient, file) ->
        ShareFilePathImpl(file, UserID(user), DatasetID(dataset), UserID(recipient), bucket)
      }
  }

  private class ShareFilePathImpl(
    override val fileName: String,
    override val userID: UserID,
    override val datasetID: DatasetID,
    override val recipientID: UserID,
    override val bucketName: String,
  ) : ShareFilePath {
    override val pathString: String
      get() = "$bucketName/$userID/$datasetID/${FileName.ShareDirectory}/$recipientID/$fileName"

    override fun toString() = pathString
  }
}

