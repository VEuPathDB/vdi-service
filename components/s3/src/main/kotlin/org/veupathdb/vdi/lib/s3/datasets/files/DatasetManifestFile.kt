package org.veupathdb.vdi.lib.s3.datasets.files

import org.veupathdb.vdi.lib.common.model.VDIDatasetManifest

/**
 * Represents a possible manifest JSON file that may exist in a target
 * [DatasetDirectory].
 */
interface DatasetManifestFile : DatasetFile {

  /**
   * Attempts to load and parse the contents of the manifest JSON file.
   *
   * If the manifest JSON file does not exist at the time this method is
   * called, `null` will be returned.
   *
   * @return The parsed contents of the target manifest JSON file, if it
   * exists, otherwise `null`.
   */
  fun load(): VDIDatasetManifest?
}