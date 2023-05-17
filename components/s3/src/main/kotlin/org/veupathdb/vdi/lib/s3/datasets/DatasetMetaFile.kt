package org.veupathdb.vdi.lib.s3.datasets

import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta

/**
 * Represents a possible `meta.json` file that may exist in a target
 * [DatasetDirectory].
 */
interface DatasetMetaFile : DatasetFile {

  /**
   * Attempts to load and parse the contents of the `meta.json` file.
   *
   * If the `meta.json` file does not exist at the time this method is called,
   * `null` will be returned.
   *
   * @return The parsed contents of the target `meta.json` file, if it exists,
   * otherwise `null`.
   */
  fun load(): VDIDatasetMeta?
}