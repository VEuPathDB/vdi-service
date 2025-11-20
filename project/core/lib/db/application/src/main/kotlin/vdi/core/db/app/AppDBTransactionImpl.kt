package vdi.core.db.app

import org.slf4j.LoggerFactory
import java.sql.Connection
import java.time.OffsetDateTime
import vdi.core.db.app.model.*
import vdi.core.db.app.sql.dataset.*
import vdi.core.db.app.sql.dataset_associated_factor.deleteAssociatedFactors
import vdi.core.db.app.sql.dataset_associated_factor.insertAssociatedFactors
import vdi.core.db.app.sql.dataset_bioproject_id.deleteBioprojectIDs
import vdi.core.db.app.sql.dataset_bioproject_id.insertBioprojectIDs
import vdi.core.db.app.sql.dataset_characteristics.deleteDatasetCharacteristics
import vdi.core.db.app.sql.dataset_characteristics.insertDatasetCharacteristics
import vdi.core.db.app.sql.dataset_contact.deleteDatasetContacts
import vdi.core.db.app.sql.dataset_contact.insertDatasetContacts
import vdi.core.db.app.sql.dataset_country.deleteCountries
import vdi.core.db.app.sql.dataset_country.insertCountries
import vdi.core.db.app.sql.dataset_dependency.deleteDatasetDependencies
import vdi.core.db.app.sql.dataset_dependency.insertDatasetDependencies
import vdi.core.db.app.sql.dataset_disease.deleteDiseases
import vdi.core.db.app.sql.dataset_disease.insertDiseases
import vdi.core.db.app.sql.dataset_doi.deleteDOIs
import vdi.core.db.app.sql.dataset_doi.insertDOIs
import vdi.core.db.app.sql.dataset_funding_award.deleteFundingAwards
import vdi.core.db.app.sql.dataset_funding_award.insertFundingAwards
import vdi.core.db.app.sql.dataset_hyperlink.deleteDatasetHyperlinks
import vdi.core.db.app.sql.dataset_hyperlink.insertDatasetHyperlinks
import vdi.core.db.app.sql.dataset_install_message.*
import vdi.core.db.app.sql.dataset_link.deleteLinks
import vdi.core.db.app.sql.dataset_link.insertLinks
import vdi.core.db.app.sql.dataset_meta.deleteDatasetMeta
import vdi.core.db.app.sql.dataset_meta.insertDatasetMeta
import vdi.core.db.app.sql.dataset_meta.updateDatasetMeta
import vdi.core.db.app.sql.dataset_organism.deleteDatasetOrganisms
import vdi.core.db.app.sql.dataset_organism.insertExperimentalOrganism
import vdi.core.db.app.sql.dataset_organism.insertHostOrganism
import vdi.core.db.app.sql.dataset_project.*
import vdi.core.db.app.sql.dataset_publication.deleteDatasetPublications
import vdi.core.db.app.sql.dataset_publication.insertDatasetPublications
import vdi.core.db.app.sql.dataset_sample_type.deleteSampleTypes
import vdi.core.db.app.sql.dataset_sample_type.insertSampleTypes
import vdi.core.db.app.sql.dataset_species.deleteSpecies
import vdi.core.db.app.sql.dataset_species.insertSpecies
import vdi.core.db.app.sql.dataset_visibility.*
import vdi.core.db.app.sql.sync_control.*
import vdi.core.db.model.SyncControlRecord
import vdi.model.meta.*

abstract class AppDBTransactionImpl(
  override val project: InstallTargetID,
  protected val schema: String,
  protected val connection: Connection,
): AppDBTransaction {
  private val logger = LoggerFactory.getLogger(javaClass)

  init {
    connection.autoCommit = false
  }

  // region dataset

  override fun deleteDataset(datasetID: DatasetID) =
    (connection.deleteDataset(schema, datasetID) > 0)
      .also { if (it) logger.debug("deleted dataset record for {}", datasetID) }

  override fun selectDataset(datasetID: DatasetID) =
    connection.selectDataset(schema, datasetID)

  override fun selectDatasetsByInstallStatus(installType: InstallType, installStatus: InstallStatus) =
    connection.selectDatasetsByInstallStatus(schema, installType, installStatus, project)

  override fun updateDataset(dataset: DatasetRecord) =
    (connection.updateDataset(schema, dataset) > 0)
      .also { if (it) logger.debug("updated dataset record for {}", dataset.datasetID) }

  override fun updateDatasetDeletedFlag(datasetID: DatasetID, deleteFlag: DeleteFlag) =
    (connection.updateDatasetDeletedFlag(schema, datasetID, deleteFlag) > 0)
      .also { if (it) logger.debug("updated deletion status for dataset {} to {}", datasetID, deleteFlag) }

  // endregion dataset

  // region dataset_associated_factor

  override fun deleteAssociatedFactors(datasetID: DatasetID) =
    connection.deleteAssociatedFactors(schema, datasetID)
      .also { if (it > 0) logger.debug("deleted {} dataset_associated_factor records from dataset {}", it, datasetID) }

  override fun insertAssociatedFactors(datasetID: DatasetID, factors: Iterable<String>) =
    connection.insertAssociatedFactors(schema, datasetID, factors)
      .also { if (it > 0) logger.debug("inserted {} dataset_associated_factor records for dataset {}", it, datasetID) }

  // endregion dataset_associated_factor

  // region dataset_bioproject_id

  override fun insertBioprojectIDs(datasetID: DatasetID, ids: Iterable<BioprojectIDReference>) =
    connection.insertBioprojectIDs(schema, datasetID, ids)
      .also { if (it > 0) logger.debug("inserted {} dataset_bioproject_id records for dataset {}", it, datasetID) }

  override fun deleteBioprojectIDs(datasetID: DatasetID) =
    connection.deleteBioprojectIDs(schema, datasetID)
      .also { if (it > 0) logger.debug("deleted {} dataset_bioproject_id records from dataset {}", it, datasetID) }

  // endregion dataset_bioproject_id

  // region dataset_characteristics

  override fun insertCharacteristics(datasetID: DatasetID, characteristics: DatasetCharacteristics) =
    (connection.insertDatasetCharacteristics(schema, datasetID, characteristics) > 0)
      .also { if (it) logger.debug("inserted dataset_characteristics record for dataset {}", datasetID) }

  override fun deleteCharacteristics(datasetID: DatasetID) =
    (connection.deleteDatasetCharacteristics(schema, datasetID) > 0)
      .also { if (it) logger.debug("deleted dataset_characteristics record from dataset {}", datasetID) }

  // endregion dataset_characteristics

  // region dataset_contact

  override fun deleteContacts(datasetID: DatasetID) =
    connection.deleteDatasetContacts(schema, datasetID)
      .also { if (it > 0) logger.debug("deleted {} dataset_contact records from dataset {}", it, datasetID) }

  override fun insertDatasetContacts(datasetID: DatasetID, contacts: Iterable<DatasetContact>) =
    connection.insertDatasetContacts(schema, datasetID, contacts)
      .also { if (it > 0) logger.debug("inserted {} dataset_contact records for dataset {}", it, datasetID) }

  // endregion dataset_contact

  // region dataset_country

  override fun deleteCountries(datasetID: DatasetID) =
    connection.deleteCountries(schema, datasetID)
      .also { if (it > 0) logger.debug("deleted {} dataset_country records from dataset {}", it, datasetID) }

  override fun insertCountries(datasetID: DatasetID, countries: Iterable<String>) =
    connection.insertCountries(schema, datasetID, countries)
      .also { if (it > 0) logger.debug("inserted {} dataset_country records for dataset {}", it, datasetID) }

  // endregion dataset_country

  // region dataset_dependency

  override fun deleteDependencies(datasetID: DatasetID) =
    connection.deleteDatasetDependencies(schema, datasetID)
      .also { if (it > 0) logger.debug("deleted {} dataset_dependency records from dataset {}", it, datasetID) }

  override fun insertDatasetDependencies(datasetID: DatasetID, dependencies: Iterable<DatasetDependency>) =
    connection.insertDatasetDependencies(schema, datasetID, dependencies)
      .also { if (it > 0) logger.debug("inserted {} dataset_dependency records for dataset {}", it, datasetID) }

  // endregion dataset_dependency

  // region dataset_disease

  override fun deleteDiseases(datasetID: DatasetID) =
    connection.deleteDiseases(schema, datasetID)
      .also { if (it > 0) logger.debug("deleted {} dataset_disease records from dataset {}", it, datasetID) }

  override fun insertDiseases(datasetID: DatasetID, diseases: Iterable<String>) =
    connection.insertDiseases(schema, datasetID, diseases)
      .also { if (it > 0) logger.debug("inserted {} dataset_disease records for dataset {}", it, datasetID) }

  // endregion dataset_disease

  // region dataset_dois

  override fun deleteDOIs(datasetID: DatasetID) =
    connection.deleteDOIs(schema, datasetID)
      .also { if (it > 0) logger.debug("deleted {} dataset_doi records from dataset {}", it, datasetID) }

  override fun insertDOIs(datasetID: DatasetID, dois: Iterable<DOIReference>) =
    connection.insertDOIs(schema, datasetID, dois)
      .also { if (it > 0) logger.debug("inserted {} dataset_doi records for dataset {}", it, datasetID) }

  // endregion dataset_dois

  // region dataset_funding_award

  override fun deleteFundingAwards(datasetID: DatasetID) =
    connection.deleteFundingAwards(schema, datasetID)
      .also { if (it > 0) logger.debug("deleted {} dataset_funding_award records from dataset {}", it, datasetID) }

  override fun insertFundingAwards(datasetID: DatasetID, awards: Iterable<DatasetFundingAward>) =
    connection.insertFundingAwards(schema, datasetID, awards)
      .also { if (it > 0) logger.debug("inserted {} dataset_funding_award records for dataset {}", it, datasetID) }

  // endregion dataset_funding_award

  // region dataset_hyperlink

  override fun deleteHyperlinks(datasetID: DatasetID) =
    connection.deleteDatasetHyperlinks(schema, datasetID)
      .also { if (it > 0) logger.debug("deleted {} dataset_hyperlink records from dataset {}", it, datasetID) }

  override fun insertHyperlinks(datasetID: DatasetID, hyperlinks: Iterable<DatasetHyperlink>) =
    connection.insertDatasetHyperlinks(schema, datasetID, hyperlinks)
      .also { if (it > 0) logger.debug("inserted {} dataset_hyperlink records for dataset {}", it, datasetID) }

  // endregion dataset_hyperlink

  // region dataset_install_message

  override fun deleteInstallMessage(datasetID: DatasetID, installType: InstallType) {
    connection.deleteInstallMessage(schema, datasetID, installType)
  }

  override fun deleteInstallMessages(datasetID: DatasetID) {
    connection.deleteInstallMessages(schema, datasetID)
  }

  override fun insertDatasetInstallMessage(message: DatasetInstallMessage) {
    connection.insertDatasetInstallMessage(schema, message)
  }

  override fun selectDatasetInstallMessage(datasetID: DatasetID, installType: InstallType) =
    connection.selectDatasetInstallMessage(schema, datasetID, installType)

  override fun selectDatasetInstallMessages(datasetID: DatasetID) =
    connection.selectDatasetInstallMessages(schema, datasetID)

  override fun updateDatasetInstallMessage(message: DatasetInstallMessage) {
    connection.updateDatasetInstallMessage(schema, message)
  }

  // endregion dataset_install_message

  // region dataset_link

  override fun deleteExternalDatasetLinks(datasetID: DatasetID) =
    connection.deleteLinks(schema, datasetID)
      .also { if (it > 0) logger.debug("deleted {} dataset_link records from dataset {}", it, datasetID) }

  override fun insertExternalDatasetLinks(datasetID: DatasetID, otherDatasets: Iterable<LinkedDataset>) =
    connection.insertLinks(schema, datasetID, otherDatasets)
      .also { if (it > 0) logger.debug("inserted {} dataset_link records for dataset {}", it, datasetID) }

  // endregion dataset_link

  // region dataset_meta

  override fun deleteDatasetMeta(datasetID: DatasetID) {
    connection.deleteDatasetMeta(schema, datasetID)
  }

  override fun insertDatasetMeta(datasetID: DatasetID, meta: DatasetMetadata) {
    connection.insertDatasetMeta(schema, datasetID, meta)
  }

  override fun updateDatasetMeta(datasetID: DatasetID, meta: DatasetMetadata) {
    connection.updateDatasetMeta(schema, datasetID, meta)
  }

  // endregion dataset_meta

  // region dataset_organism

  override fun deleteDatasetOrganisms(datasetID: DatasetID) =
    connection.deleteDatasetOrganisms(schema, datasetID)
      .also { if (it > 0) logger.debug("deleted {} dataset_organism records from dataset {}", it, datasetID) }

  override fun insertExperimentalOrganism(datasetID: DatasetID, organism: DatasetOrganism) =
    (connection.insertExperimentalOrganism(schema, datasetID, organism) > 0)
      .also { if (it) logger.debug("inserted experimental dataset_organism record for dataset {}", datasetID) }

  override fun insertHostOrganism(datasetID: DatasetID, organism: DatasetOrganism) =
    (connection.insertHostOrganism(schema, datasetID, organism) > 0)
      .also { if (it) logger.debug("inserted host dataset_organism record for dataset {}", datasetID) }

  // endregion dataset_organism

  // region dataset_project

  override fun deleteDatasetProjectLink(datasetID: DatasetID, installTarget: InstallTargetID) {
    connection.deleteDatasetProjectLink(schema, datasetID, installTarget)
  }

  override fun deleteDatasetProjectLinks(datasetID: DatasetID) {
    connection.deleteDatasetProjectLinks(schema, datasetID)
  }

  override fun insertDatasetProjectLink(datasetID: DatasetID, installTarget: InstallTargetID) {
    connection.insertDatasetProjectLink(schema, datasetID, installTarget)
  }

  override fun insertDatasetProjectLinks(datasetID: DatasetID, projectIDs: Iterable<InstallTargetID>) {
    connection.insertDatasetProjectLinks(schema, datasetID, projectIDs)
  }

  override fun selectDatasetProjectLinks(datasetID: DatasetID) =
    connection.selectDatasetProjectLinks(schema, datasetID)

  override fun testDatasetProjectLinkExists(datasetID: DatasetID, installTarget: InstallTargetID) =
    connection.testDatasetProjectLinkExists(schema, datasetID, installTarget)

  // endregion dataset_project

  // region dataset_publication

  override fun deleteDatasetPublications(datasetID: DatasetID) =
    connection.deleteDatasetPublications(schema, datasetID)
      .also { if (it > 0) logger.debug("deleted {} dataset_publication records from dataset {}", it, datasetID) }

  override fun insertDatasetPublications(datasetID: DatasetID, publications: Iterable<DatasetPublication>) =
    connection.insertDatasetPublications(schema, datasetID, publications)
      .also { if (it > 0) logger.debug("inserted {} dataset_publication records for dataset {}", it, datasetID) }

  // endregion dataset_publication

  // region dataset_sample_type

  override fun deleteSampleTypes(datasetID: DatasetID) =
    connection.deleteSampleTypes(schema, datasetID)
      .also { if (it > 0) logger.debug("deleted {} dataset_sample_type records from dataset {}", it, datasetID) }

  override fun insertSampleTypes(datasetID: DatasetID, sampleTypes: Iterable<String>) =
    connection.insertSampleTypes(schema, datasetID, sampleTypes)
      .also { if (it > 0) logger.debug("inserted {} dataset_sample_type records for dataset {}", it, datasetID) }

  // endregion dataset_sample_type

  // region dataset_species

  override fun deleteSpecies(datasetID: DatasetID) =
    connection.deleteSpecies(schema, datasetID)
      .also { if (it > 0) logger.debug("deleted {} dataset_species records from dataset {}", it, datasetID) }

  override fun insertSpecies(datasetID: DatasetID, species: Iterable<String>) =
    connection.insertSpecies(schema, datasetID, species)
      .also { if (it > 0) logger.debug("inserted {} dataset_species records for dataset {}", it, datasetID) }

  // endregion dataset_species

  // region dataset_visibility

  override fun deleteDatasetVisibility(datasetID: DatasetID, userID: UserID) =
    (connection.deleteDatasetVisibility(schema, datasetID, userID) > 0)
      .also { if (it) logger.debug("deleted dataset_visibility record for user {} from dataset {}", userID, datasetID) }

  override fun deleteDatasetVisibilities(datasetID: DatasetID) =
    connection.deleteDatasetVisibilities(schema, datasetID)
      .also { if (it > 0) logger.debug("deleted {} dataset_visibility records from dataset {}", it, datasetID) }

  override fun insertDatasetVisibility(datasetID: DatasetID, userID: UserID) {
    connection.insertDatasetVisibility(schema, datasetID, userID)
  }

  override fun selectDatasetVisibilityRecords(datasetID: DatasetID) =
    connection.selectDatasetVisibilityRecords(schema, datasetID)

  override fun testDatasetVisibilityExists(datasetID: DatasetID, userID: UserID) =
    connection.testDatasetVisibilityExists(schema, datasetID, userID)

  // endregion dataset_visibility

  // region sync_control

  override fun deleteSyncControl(datasetID: DatasetID) {
    connection.deleteSyncControl(schema, datasetID)
  }

  override fun insertDatasetSyncControl(sync: SyncControlRecord) {
    connection.insertDatasetSyncControl(schema, sync)
  }

  override fun selectDatasetSyncControlRecord(datasetID: DatasetID) =
    connection.selectSyncControl(schema, datasetID)

  override fun streamAllSyncControlRecords() =
    connection.selectAllSyncControl(schema, platform)

  override fun updateSyncControlDataTimestamp(datasetID: DatasetID, timestamp: OffsetDateTime) {
    connection.updateSyncControlDataTimestamp(schema, datasetID, timestamp)
  }

  override fun updateSyncControlMetaTimestamp(datasetID: DatasetID, timestamp: OffsetDateTime) {
    connection.updateSyncControlMetaTimestamp(schema, datasetID, timestamp)
  }

  override fun updateSyncControlSharesTimestamp(datasetID: DatasetID, timestamp: OffsetDateTime) {
    connection.updateSyncControlSharesTimestamp(schema, datasetID, timestamp)
  }

  // endregion sync_control

  override fun rollback() {
    connection.rollback()
  }

  override fun commit() {
    connection.commit()
  }

  override fun close() {
    connection.close()
  }
}
