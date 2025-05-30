package vdi.lib.s3.files

import org.veupathdb.vdi.lib.common.model.VDIDatasetMeta
import vdi.lib.s3.DatasetDirectory


/**
 * Represents a possible metadata JSON file that may exist in a target
 * [DatasetDirectory].
 */
interface DatasetMetaFile : DatasetFile {

  /**
   * Attempts to load and parse the contents of the metadata JSON file.
   *
   * If the metadata JSON file does not exist at the time this method is called,
   * `null` will be returned.
   *
   * @return The parsed contents of the target metadata JSON file, if it exists,
   * otherwise `null`.
   */
  fun load(): VDIDatasetMeta?
}
