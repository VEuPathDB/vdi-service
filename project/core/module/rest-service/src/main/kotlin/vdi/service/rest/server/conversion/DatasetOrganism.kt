package vdi.service.rest.server.conversion

import vdi.model.meta.DatasetOrganism as InternalType
import vdi.service.rest.generated.model.DatasetOrganism as RamlType
import vdi.service.rest.generated.model.DatasetOrganismImpl

internal fun DatasetOrganism(organism: InternalType): RamlType =
  DatasetOrganismImpl().also {
    it.species = organism.species
    it.strain  = organism.strain
  }
