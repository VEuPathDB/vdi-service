package org.veupathdb.service.vdi.server.services.dataset

import org.veupathdb.lib.container.jaxrs.providers.UserProvider
import org.veupathdb.service.vdi.generated.resources.DatasetsVdiId.GetDatasetsByVdiIdResponse
import org.veupathdb.service.vdi.generated.resources.DatasetsVdiId.GetDatasetsByVdiIdResponse.*
import org.veupathdb.service.vdi.server.outputs.toExternal
import org.veupathdb.service.vdi.model.UserDetails
import org.veupathdb.service.vdi.s3.DatasetStore
import org.veupathdb.service.vdi.server.outputs.*
import org.veupathdb.service.vdi.util.defaultZone
import org.veupathdb.vdi.lib.common.field.DatasetID
import org.veupathdb.vdi.lib.common.field.UserID
import org.veupathdb.vdi.lib.common.model.VDIDatasetPublication
import org.veupathdb.vdi.lib.common.model.VDIDatasetVisibility
import vdi.component.db.app.AppDB
import vdi.component.db.cache.CacheDB
import vdi.component.db.cache.model.DatasetShare
import vdi.lib.logging.logger
import vdi.lib.plugin.registry.PluginRegistry

/**
 * Admin-auth endpoint for looking up a dataset by ID.  In this case we don't
 * return user information.
 */
fun adminGetDatasetByID(datasetID: DatasetID) =
  getDatasetByID(null, datasetID, true)

fun getDatasetByID(userID: UserID?, datasetID: DatasetID, includeDeleted: Boolean = false): GetDatasetsByVdiIdResponse {
  // Lookup dataset that is owned by or shared with the current user
  val dataset = when {
    userID == null -> CacheDB().selectDataset(datasetID)
    else           -> lookupDatasetForUser(userID, datasetID)
  } ?: return respond404WithApplicationJson(NotFoundError())

  if (!includeDeleted && userID != null && dataset.isDeleted)
    return respond404WithApplicationJson(NotFoundError())

  val shares = if (dataset.ownerID == userID) {
    CacheDB().selectSharesForDataset(datasetID)
  } else {
    emptyList()
  }

  val userDetails = getUserDetails(dataset.ownerID, shares, userID)

  val typeDisplayName = PluginRegistry[dataset.typeName, dataset.typeVersion]?.displayName
    .let {
      if (it == null) {
        logger("getDatasetByID").error(
          "plugin is disabled for requested dataset {}/{} type {} (version {})",
          dataset.ownerID,
          dataset.datasetID,
          dataset.typeName,
          dataset.typeVersion,
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

  return respond200WithApplicationJson(DatasetDetails(
    datasetID        = datasetID,
    owner            = DatasetOwner(userDetails.requireDetails(dataset.ownerID)),
    datasetType      = DatasetTypeResponseBody(dataset, typeDisplayName),
    name             = dataset.name,
    origin           = dataset.origin,
    projectIDs       = dataset.projects,
    visibility       = DatasetVisibility(dataset.visibility),
    status           = DatasetStatusInfo(
      dataset.importStatus,
      AppDB().getDatasetStatuses(dataset.datasetID, dataset.projects)
    ),
    created          = dataset.created.defaultZone(),
    dependencies     = metaJson?.dependencies?.let(::DatasetDependencies) ?: emptyList(),
    shortName        = dataset.shortName,
    shortAttribution = dataset.shortAttribution,
    category         = dataset.category,
    summary          = dataset.summary,
    description      = dataset.description,
    sourceURL        = dataset.sourceURL,
    importMessages   = CacheDB().selectImportMessages(datasetID),
    shares           = shares.mapNotNull { s ->
      s.offerStatus?.let { ShareOffer(userDetails.requireDetails(s.recipientID), it)
    } },
    publications     = metaJson?.publications?.map(VDIDatasetPublication::toExternal),
    hyperlinks       = metaJson?.hyperlinks?.map(::DatasetHyperlink),
    organisms        = metaJson?.organisms?.toList(),
    contacts         = metaJson?.contacts?.map(::DatasetContact),
    originalID       = revisions?.originalID,
    revisionHistory  = revisions?.records?.map { DatasetRevision(it, revisionIndex[it.revisionID]?.revisionNote) }
  ))
}

internal fun getLatestRevision(datasetID: DatasetID, fn: (DatasetID) -> String) =
  CacheDB().selectLatestRevision(datasetID)
    ?.let { respond301(headersFor301().withLocation(fn(it.revisionID))) }

private fun lookupDatasetForUser(userID: UserID, datasetID: DatasetID) =
  with(CacheDB()) {
    selectDatasetForUser(userID, datasetID) ?: selectDataset(datasetID)
      ?.takeUnless { it.visibility == VDIDatasetVisibility.Private }
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
