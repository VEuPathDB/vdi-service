package org.veupathdb.service.vdi.genx.model

import org.veupathdb.service.vdi.generated.model.ShareOfferEntry
import org.veupathdb.service.vdi.generated.model.ShareOfferEntryImpl
import org.veupathdb.service.vdi.model.ShareFilterStatus
import org.veupathdb.service.vdi.model.UserDetails
import org.veupathdb.vdi.lib.common.field.DataType
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID

internal fun ShareOfferEntry(
  datasetID:   DatasetID,
  shareStatus: ShareFilterStatus,
  datasetTypeName: DataType,
  datasetTypeVersion: String,
  datasetTypeDisplayName: String,
  owner:       UserDetails,
  projectIDs:  List<ProjectID>
): ShareOfferEntry =
  ShareOfferEntryImpl()
    .also {
      it.datasetId   = datasetID.toString()
      it.shareStatus = ShareOfferStatus(shareStatus)
      it.datasetType = DatasetTypeInfo(datasetTypeName, datasetTypeVersion, datasetTypeDisplayName)
      it.owner       = DatasetOwner(owner)
      it.projectIds  = projectIDs
    }
