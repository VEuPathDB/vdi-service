package org.veupathdb.vdi.lib.db.app

import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDISyncControlRecord
import org.veupathdb.vdi.lib.db.app.model.*

interface AppDBAccessor {

  /**
   * Looks up a dataset record for the target dataset.
   *
   * @param datasetID ID of the dataset to look up.
   *
   * @return The located [DatasetRecord] or `null` if no matching record could
   * be found.
   */
  fun selectDataset(datasetID: DatasetID): DatasetRecord?

  /**
   * Looks up a dataset install message record for the target dataset and
   * install type.
   *
   * @param datasetID ID of the target dataset whose install message record
   * should be retrieved.
   *
   * @param installType Type of the installation for the install message record
   * to retrieve.
   *
   * @return The located [DatasetInstallMessage] record, or `null` if no
   * matching record could be found.
   */
  fun selectDatasetInstallMessage(datasetID: DatasetID, installType: InstallType): DatasetInstallMessage?

  /**
   * Looks up all dataset install message records for the target dataset.
   *
   * @param datasetID ID of the target dataset whose install message records
   * should be retrieved.
   *
   * @return A list of zero or more [DatasetInstallMessage] records.
   */
  fun selectDatasetInstallMessages(datasetID: DatasetID): List<DatasetInstallMessage>

  /**
   * Looks up a dataset sync control record for the target dataset.
   *
   * @param datasetID ID of the target dataset whose sync control record should
   * be retrieved.
   *
   * @return The located [VDISyncControlRecord], or `null` if no matching record
   * could be found.
   */
  fun selectDatasetSyncControlRecord(datasetID: DatasetID): VDISyncControlRecord?

  /**
   * Retrieves all dataset visibility records for the target dataset.
   *
   * @param datasetID ID of the target dataset whose visibility records should
   * be retrieved.
   *
   * @return A list of zero or more [DatasetVisibilityRecord] instances
   * representing rows in the target database table.
   */
  fun selectDatasetVisibilityRecords(datasetID: DatasetID): List<DatasetVisibilityRecord>

  /**
   * Retrieves all dataset-to-project link records for the target dataset.
   *
   * @param datasetID ID of the target dataset whose project link records should
   * be retrieved.
   *
   * @return A list of zero or more [DatasetProjectLinkRecord] instances
   * representing rows in the target database table.
   */
  fun selectDatasetProjectLinks(datasetID: DatasetID): List<DatasetProjectLinkRecord>

  /**
   * Tests whether a dataset visibility record exists for a target dataset and
   * user.
   *
   * @param datasetID ID of the dataset whose visibility record should be tested
   * for.
   *
   * @param userID ID of the user whose visibility record should be tested for.
   *
   * @return `true` if a dataset visibility record was found for the target
   * dataset and user, otherwise `false`.
   */
  fun testDatasetVisibilityExists(datasetID: DatasetID, userID: UserID): Boolean

  /**
   * Tests whether a dataset-to-project link record exists for a target dataset
   * and project.
   *
   * @param datasetID ID of the dataset whose project link record should be
   * tested for.
   *
   * @param projectID ID of the target project that the link record should
   * match.
   *
   * @return `true` if a matching dataset-to-project link record was found,
   * otherwise `false`.
   */
  fun testDatasetProjectLinkExists(datasetID: DatasetID, projectID: ProjectID): Boolean
}