@file:JvmName("DatasetLookupService")
package vdi.service.rest.server.services.dataset

import org.veupathdb.lib.container.jaxrs.providers.UserProvider
import vdi.core.db.app.AppDB
import vdi.core.db.cache.CacheDB
import vdi.core.db.cache.model.DatasetShare
import vdi.core.plugin.registry.PluginRegistry
import vdi.model.data.DatasetID
import vdi.model.data.DatasetPublication
import vdi.model.data.DatasetVisibility
import vdi.model.data.UserID
import vdi.service.rest.generated.model.DatasetDetails
import vdi.service.rest.generated.resources.DatasetsVdiId.GetDatasetsByVdiIdResponse
import vdi.service.rest.generated.resources.DatasetsVdiId.GetDatasetsByVdiIdResponse.headersFor301
import vdi.service.rest.generated.resources.DatasetsVdiId.GetDatasetsByVdiIdResponse.respond301
import vdi.service.rest.model.UserDetails
import vdi.service.rest.s3.DatasetStore
import vdi.service.rest.server.controllers.ControllerBase
import vdi.service.rest.server.outputs.*
import vdi.service.rest.util.Either
import vdi.service.rest.util.Either.Companion.left
import vdi.service.rest.util.Either.Companion.right

/**
 * Admin-auth endpoint for looking up a dataset by ID.  In this case we don't
 * return user information.
 */
fun <T: ControllerBase> T.adminGetDatasetByID(datasetID: DatasetID) =
  getDatasetByID(null, datasetID, true)

fun <T: ControllerBase> T.userGetDatasetByID(datasetID: DatasetID) =
  getDatasetByID(userID, datasetID, false)

private fun <T: ControllerBase> T.getDatasetByID(
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
    else           -> lookupDatasetForUser(userID, datasetID)
  } ?: return right(Static404.wrap())

  if (!includeDeleted && userID != null && dataset.isDeleted)
    return right(Static404.wrap())

  val shares = if (dataset.ownerID == userID) {
    CacheDB().selectSharesForDataset(datasetID)
  } else {
    emptyList()
  }

  val userDetails = getUserDetails(dataset.ownerID, shares, userID)

  val typeDisplayName = PluginRegistry[dataset.type]?.displayName
    .let {
      if (it == null) {
        logger.error(
          "plugin is disabled for requested dataset {}/{} type {}",
          dataset.ownerID,
          dataset.datasetID,
          dataset.type,
        )
        "disabled"
      } else {
        it
      }
    }

  val metaJson = DatasetStore.getDatasetMeta(dataset.ownerID, dataset.datasetID)

  val revisions = CacheDB().selectRevisions(datasetID)

  val revisionIndex = metaJson?.revisionHistory
    ?.associateBy { it.revisionID }
    ?: emptyMap()

  return left(DatasetDetails(
    datasetID      = datasetID,
    meta           = TODO(),
    importStatus   = TODO(),
    importMessages = TODO(),
    shares         = TODO(),
    installs       = TODO(),
    userInfo       = TODO()
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
