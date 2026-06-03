package vdi.service.rest.server.conversion

import vdi.model.meta.DatasetFundingAward as InternalType
import vdi.service.rest.generated.model.DatasetFundingAward as RamlType
import vdi.service.rest.generated.model.DatasetFundingAwardImpl

internal fun DatasetFundingAward(award: InternalType): RamlType =
  DatasetFundingAwardImpl().also {
    it.agency      = award.agency
    it.awardNumber = award.awardNumber
  }