package vdi.lib.db.app.model

import org.veupathdb.vdi.lib.common.field.DatasetID
import java.time.OffsetDateTime

/**
 * Represents a single record in the `vdi.dataset_install_messages` table.
 *
 * Records in this table declare the installation status of metadata and/or
 * dataset data for a target dataset and optionally include installation
 * messages such as warnings or failure messages.
 */
data class DatasetInstallMessage(

  /**
   * ID of the target dataset.
   */
  val datasetID: DatasetID,

  /**
   * Whether this record represents a metadata installation or a dataset data
   * installation.
   */
  val installType: InstallType,

  /**
   * The current status of the installation.
   */
  val status: InstallStatus,

  /**
   * Optional message about the installation.
   *
   * For successful installations, a message here is one or more newline
   * delimited warnings about the installation.
   *
   * For a failed installation, a message here is one or more newline delimited
   * failure messages about the installation.
   */
  val message: String?,

  /**
   * Timestamp of the message and status.
   */
  val updated: OffsetDateTime = OffsetDateTime.now(),
)
