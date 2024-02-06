package org.veupathdb.vdi.lib.s3.datasets.files

import java.io.InputStream

interface DatasetImportableFile : DatasetFile {
  /**
   * Attempts to open a stream over the contents of the target dataset upload
   * file object.
   *
   * @return An input stream over the contents of the target upload file object
   * if it exists, otherwise `null`.
   */
  fun open(): InputStream?
}