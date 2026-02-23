package vdi.service.rest.conversion;

import org.jetbrains.annotations.NotNull;
import vdi.service.rest.generated.model.BioprojectIDReferenceImpl;

public class BioprojectIDReference extends BioprojectIDReferenceImpl {
  public BioprojectIDReference(@NotNull vdi.model.meta.BioprojectIDReference reference) {
    setId(reference.getId());
    setDescription(reference.getDescription());
  }
}
