package vdi.component.s3.paths

import org.veupathdb.vdi.lib.common.field.UserID

sealed interface VDDatasetShareFilePath : VDPath {
  val recipientID: UserID
  val fileName: String

  val isOffer: Boolean

  val isReceipt: Boolean
}

