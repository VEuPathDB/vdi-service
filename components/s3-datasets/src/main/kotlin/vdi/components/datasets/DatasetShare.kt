package vdi.components.datasets

interface DatasetShare {

  /**
   * ID of the share recipient.
   */
  val recipientID: String

  /**
   * A handle on this share offer's offer file.
   */
  val offer: DatasetShareOfferFile

  /**
   * A handle on this share offer's receipt file.
   */
  val receipt: DatasetShareReceiptFile
}

