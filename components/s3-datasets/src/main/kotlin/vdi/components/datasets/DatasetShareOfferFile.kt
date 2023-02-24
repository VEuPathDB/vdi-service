package vdi.components.datasets

import vdi.components.datasets.model.DatasetShareOffer

/**
 * Represents a possible dataset share offer file that may exist in a target
 * [DatasetDirectory].
 */
interface DatasetShareOfferFile : DatasetFile {

  /**
   * Attempts to load and parse the contents of the dataset share offer file.
   *
   * If the dataset share offer file does not exist at the time this method is
   * called, `null` will be returned.
   *
   * @return The parsed contents of the dataset share offer file, if it exists,
   * otherwise `null`.
   */
  fun load(): DatasetShareOffer?
}
