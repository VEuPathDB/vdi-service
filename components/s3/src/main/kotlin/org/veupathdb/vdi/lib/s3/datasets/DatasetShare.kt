package org.veupathdb.vdi.lib.s3.datasets

import org.veupathdb.vdi.lib.common.field.UserID

interface DatasetShare {

  /**
   * ID of the share recipient.
   */
  val recipientID: UserID

  /**
   * A handle on this share offer's offer file.
   */
  val offer: DatasetShareOfferFile

  /**
   * A handle on this share offer's receipt file.
   */
  val receipt: DatasetShareReceiptFile
}

