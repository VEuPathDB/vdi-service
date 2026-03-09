package vdi.service.rest.conversion;

import vdi.service.rest.generated.model.BioprojectIDReference;
import vdi.service.rest.generated.model.DatasetOrganism;

public final class InputTypes {
  public static vdi.model.meta.BioprojectIDReference BioprojectIDReference(BioprojectIDReference reference) {
    return new vdi.model.meta.BioprojectIDReference(reference.getId(), reference.getDescription());
  }

  public static vdi.model.meta.DatasetOrganism DatasetOrganism(DatasetOrganism organism) {
    return new vdi.model.meta.DatasetOrganism(organism.getSpecies(), organism.getStrain());
  }
}
