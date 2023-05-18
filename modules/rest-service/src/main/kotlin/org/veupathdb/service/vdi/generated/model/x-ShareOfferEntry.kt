package org.veupathdb.service.vdi.generated.model

import org.veupathdb.service.vdi.model.ShareFilterStatus
import org.veupathdb.service.vdi.model.UserDetails
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.ProjectID

internal fun ShareOfferEntry(
  datasetID:   DatasetID,
  shareStatus: ShareFilterStatus,
  datasetTypeName: String,
  datasetTypeVersion: String,
  owner:       UserDetails,
  projectIDs:  List<ProjectID>
): ShareOfferEntry =
  ShareOfferEntryImpl()
    .also {
      it.datasetID   = datasetID.toString()
      it.shareStatus = ShareOfferStatus(shareStatus)
      it.datasetType = DatasetTypeInfo(datasetTypeName, datasetTypeVersion)
      it.owner       = DatasetOwner(owner)
      it.projectIDs  = projectIDs
    }