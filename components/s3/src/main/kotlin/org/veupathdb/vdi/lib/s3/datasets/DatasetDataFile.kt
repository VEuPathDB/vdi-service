package org.veupathdb.vdi.lib.s3.datasets

import java.io.InputStream

/**
 * Represents a possible dataset data file object that may exist in a target
 * [DatasetDirectory].
 */
interface DatasetDataFile : DatasetFile {

  /**
   * Attempts to open a stream over the contents of the target dataset data file
   * object.
   *
   * @return An input stream over the contents of the target data file object if
   * it exists, otherwise `null`.
   */
  fun open(): InputStream?
}

