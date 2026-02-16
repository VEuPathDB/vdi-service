@file:JvmName("DatasetLookupService")
package vdi.service.rest.server.services.dataset

import org.veupathdb.lib.container.jaxrs.providers.UserProvider
import vdi.core.db.app.AppDB
import vdi.core.db.cache.CacheDB
import vdi.core.db.cache.model.DatasetShare
import vdi.core.db.cache.model.RelatedDataset
import vdi.core.util.orElse
import vdi.model.DatasetUploadStatus
import vdi.model.meta.DatasetID
import vdi.model.meta.DatasetMetadata
import vdi.model.meta.DatasetVisibility
import vdi.model.meta.UserID
import vdi.service.rest.generated.model.DatasetDetails
import vdi.service.rest.generated.resources.DatasetsVdiId.GetDatasetsByVdiIdResponse
import vdi.service.rest.generated.resources.DatasetsVdiId.GetDatasetsByVdiIdResponse.headersFor301
import vdi.service.rest.generated.resources.DatasetsVdiId.GetDatasetsByVdiIdResponse.respond301
import vdi.service.rest.model.UserDetails
import vdi.service.rest.s3.DatasetStore
import vdi.service.rest.server.controllers.ControllerBase
import vdi.service.rest.server.outputs.*
import vdi.service.rest.server.services.dataset.files.listDatasetFiles
import vdi.util.fn.Either
import vdi.util.fn.Either.Companion.left
import vdi.util.fn.Either.Companion.right

/**
 * Admin-auth endpoint for looking up a dataset by ID.  In this case we don't
 * return user information.
 */
fun adminGetDatasetByID(datasetID: DatasetID) =
  getDatasetByID(null, datasetID, true)

fun ControllerBase.userGetDatasetByID(datasetID: DatasetID) =
  getDatasetByID(userID, datasetID, false)

private fun getDatasetByID(
  userID: UserID?,
  datasetID: DatasetID,
  includeDeleted: Boolean,
): Either<DatasetDetails, GetDatasetsByVdiIdResponse> {
  // Lookup dataset that is owned by or shared with the current user
  //
  // NOTE: we don't look for the latest revision here as the controller will
  //       call a different method for that if we return 404.
  val dataset = when {
    userID == null -> CacheDB().selectDataset(datasetID)
      ?: return right(Static404.wrap())

    else -> lookupDatasetForUser(userID, datasetID) orElse {
      CacheDB().selectDataset(datasetID) ?: return right(Static404.wrap())
      return right(GoneError().wrap())
    }
  }

  if (!includeDeleted && userID != null && dataset.isDeleted)
    return right(Static404.wrap())

  val metaJson = DatasetStore.getDatasetMeta(dataset.ownerID, dataset.datasetID)
    ?: return right(TooEarlyError().wrap())

  val shares = if (dataset.ownerID == userID) {
    CacheDB().selectSharesForDataset(datasetID)
  } else {
    emptyList()
  }

  return left(DatasetDetails(
    datasetID       = datasetID,
    meta            = metaJson,
    uploadStatus    = CacheDB().selectUploadStatus(datasetID)
      // For backwards compatibility, report success for datasets whose status
      // isn't recorded.  The reconciler should remedy this.
      ?: DatasetUploadStatus.Success,
    importStatus    = CacheDB().selectImportControl(datasetID),
    importMessages  = CacheDB().selectImportMessages(datasetID),
    shares          = shares,
    installs        = AppDB().getDatasetStatuses(datasetID, metaJson.installTargets),
    userInfo        = getUserDetails(dataset.ownerID, shares, userID),
    relatedDatasets = getRelatedDatasets(datasetID, metaJson),
    files           = listDatasetFiles(dataset.ownerID, datasetID)
  ))
}

internal fun getLatestRevision(datasetID: DatasetID, fn: (DatasetID) -> String) =
  CacheDB().selectLatestRevision(datasetID)
    ?.let { respond301(headersFor301().withLocation(fn(it.revisionID))) }

private fun lookupDatasetForUser(userID: UserID, datasetID: DatasetID) =
  with(CacheDB()) {
    selectDatasetForUser(userID, datasetID) ?: selectDataset(datasetID)
      ?.takeUnless { it.visibility == DatasetVisibility.Private }
  }

private fun getUserDetails(ownerID: UserID, shares: Collection<DatasetShare>, requesterID: UserID? = null) =
  sequence {
    if (requesterID != null)
      yield(requesterID)
    yield(ownerID)
    shares.forEach { yield(it.recipientID) }
  }
    .map { it.toLong() }
    .toSet()
    .let(UserProvider::getUsersById)
    .asSequence()
    .map { (id, user) -> UserDetails(UserID(id), user.firstName, user.lastName, user.email, user.organization) }
    .associateBy { it.userID }

private fun getRelatedDatasets(datasetID: DatasetID, meta: DatasetMetadata): Sequence<RelatedDataset> =
  (meta.projectName?.let { CacheDB().selectDatasetsByProjectName(meta.owner, it) } ?: emptyList()).asSequence() +
  (meta.programName?.let { CacheDB().selectDatasetsByProgramName(meta.owner, it) } ?: emptyList()).asSequence() +
  CacheDB().selectDatasetsByCommonPublication(datasetID)