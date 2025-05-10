package vdi.lib.s3.paths

import org.veupathdb.vdi.lib.common.field.UserID

sealed interface DatasetShareFilePath : DatasetPath {
  val recipientID: UserID

  val isOffer: Boolean

  val isReceipt: Boolean
}

