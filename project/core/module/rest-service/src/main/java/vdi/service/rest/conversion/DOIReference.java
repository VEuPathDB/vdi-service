package vdi.service.rest.conversion;

import org.jetbrains.annotations.NotNull;
import vdi.service.rest.generated.model.DOIReferenceImpl;

public class DOIReference extends DOIReferenceImpl {
  public DOIReference(@NotNull vdi.model.meta.DOIReference reference) {
    setDoi(reference.getDoi());
    setDescription(reference.getDescription());
  }
}
