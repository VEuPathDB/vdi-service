package org.veupathdb.service.vdi.service.datasets

import jakarta.ws.rs.NotFoundException
import org.veupathdb.service.vdi.db.AppDB
import org.veupathdb.service.vdi.db.UserDB
import org.veupathdb.service.vdi.generated.model.*
import vdi.component.db.cache.CacheDB
import vdi.components.common.fields.DatasetID
import vdi.components.common.fields.UserID

fun getDatasetByID(userID: UserID, datasetID: DatasetID): DatasetDetails {
  // Lookup dataset that is owned by or shared with the current user
  val dataset = CacheDB.selectDatasetForUser(userID, datasetID) ?: throw NotFoundException()

  val shares = if (dataset.ownerID == userID) {
    CacheDB.selectSharesForDataset(datasetID)
  } else {
    emptyList()
  }

  // Lookup user details for the dataset's owner and any share users
  val userDetails = UserDB.lookupUserDetails(HashSet<UserID>(shares.size + 1)
    .apply {
      add(userID)
      shares.forEach { add(it.recipientID) }
    })

  // Lookup status information for the dataset
  val statuses = AppDB.getDatasetStatuses(datasetID, dataset.projects)

  val importMessages = CacheDB.selectImportMessages(datasetID)

  // return the dataset
  return DatasetDetailsImpl().also { out ->
    out.datasetID = datasetID.toString()

    out.owner = DatasetOwner(userDetails[userID] ?: throw IllegalStateException("no user details for dataset owner"))

    out.datasetType = DatasetTypeInfoImpl().also { type ->
      type.name    = dataset.typeName
      type.version = dataset.typeVersion
    }

    out.name        = dataset.name
    out.summary     = dataset.summary
    out.description = dataset.description

    out.importMessages = importMessages

    out.status = DatasetStatusInfoImpl().also { status ->
      status.import = DatasetImportStatus(dataset.importStatus)
      status.install = ArrayList(statuses.size)

      statuses.forEach { (projectID, installStatus) ->
        status.install.add(DatasetInstallStatusEntry(projectID, installStatus))
      }
    }

    out.shares = ArrayList(shares.size)

    shares.forEach { share ->
      out.shares.add(ShareOfferImpl().also { offer ->
        offer.recipient = ShareOfferRecipientImpl().also { recip ->
          val user = userDetails[share.recipientID] ?: throw IllegalStateException("no user details for share recipient")

          recip.firstName    = user.firstName
          recip.lastName     = user.lastName
          recip.organization = user.organization
          recip.email        = user.email
        }
        offer.status = ShareOfferAction(share.offerStatus)
      })
    }
  }
}