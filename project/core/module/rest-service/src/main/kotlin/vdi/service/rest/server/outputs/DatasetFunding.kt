package vdi.service.rest.server.outputs

import vdi.model.meta.DatasetFundingAward
import vdi.service.rest.generated.model.DatasetFundingAwardImpl
import vdi.service.rest.generated.model.DatasetFundingAward as APIFunding

internal fun DatasetFundingAward(award: DatasetFundingAward): APIFunding =
  DatasetFundingAwardImpl().also {
    it.agency = award.agency
    it.awardNumber = award.awardNumber
  }