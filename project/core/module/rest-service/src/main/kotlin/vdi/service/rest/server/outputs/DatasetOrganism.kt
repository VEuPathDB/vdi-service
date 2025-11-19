package vdi.service.rest.server.outputs

import vdi.model.meta.DatasetOrganism
import vdi.service.rest.generated.model.DatasetOrganismImpl
import vdi.service.rest.generated.model.DatasetOrganism as APIOrganism

internal fun DatasetOrganism(organism: DatasetOrganism): APIOrganism =
  DatasetOrganismImpl().also {
    it.species = organism.species
    it.strain  = organism.strain
  }