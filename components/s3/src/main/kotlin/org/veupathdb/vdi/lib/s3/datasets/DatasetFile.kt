package org.veupathdb.vdi.lib.s3.datasets

import java.io.InputStream
import java.time.OffsetDateTime

interface DatasetFile {

  /**
   * Name of the dataset file.
   */
  val name: String

  val path: String

  /**
   * Tests whether this [DatasetFile] currently exists.
   *
   * @return `true` if the DatasetFile exists as of the time this method was
   * called, otherwise `false`.
   */
  fun exists(): Boolean

  /**
   * Attempts to fetch the last-modified timestamp for this [DatasetFile].
   *
   * If this `DatasetFile` does not exist at the time this method is called,
   * `null` will be returned.
   *
   * @return The last-modified timestamp for this `DatasetFile`, if it exists,
   * otherwise `null`.
   */
  fun lastModified(): OffsetDateTime?

  fun loadContents(): InputStream?
}