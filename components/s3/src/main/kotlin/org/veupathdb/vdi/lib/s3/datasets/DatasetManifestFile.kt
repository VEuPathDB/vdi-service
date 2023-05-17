package org.veupathdb.vdi.lib.s3.datasets

import org.veupathdb.vdi.lib.common.model.VDIDatasetManifest

/**
 * Represents a possible `manifest.json` file that may exist in a target
 * [DatasetDirectory].
 */
interface DatasetManifestFile : DatasetFile {

  /**
   * Attempts to load and parse the contents of the `manifest.json` file.
   *
   * If the `manifest.json` file does not exist at the time this method is
   * called, `null` will be returned.
   *
   * @return The parsed contents of the target `manifest.json` file, if it
   * exists, otherwise `null`.
   */
  fun load(): VDIDatasetManifest?
}