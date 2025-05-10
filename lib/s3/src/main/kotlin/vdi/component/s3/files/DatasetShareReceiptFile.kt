package vdi.component.s3.files

import org.veupathdb.vdi.lib.common.model.VDIDatasetShareReceipt
import vdi.component.s3.DatasetDirectory

/**
 * Represents a possible dataset share receipt file that may exist in a target
 * [DatasetDirectory].
 */
interface DatasetShareReceiptFile : DatasetFile {

  /**
   * Attempts to load and parse the contents of the dataset share receipt file.
   *
   * If the dataset share receipt file does not exist at the time this method is
   * called, `null` will be returned.
   *
   * @return The parsed contents of the dataset share receipt file, if it
   * exists, otherwise `null`.
   */
  fun load(): VDIDatasetShareReceipt?
}
