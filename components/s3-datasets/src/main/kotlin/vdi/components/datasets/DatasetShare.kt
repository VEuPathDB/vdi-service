package vdi.components.datasets

import vdi.components.common.fields.UserID

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

