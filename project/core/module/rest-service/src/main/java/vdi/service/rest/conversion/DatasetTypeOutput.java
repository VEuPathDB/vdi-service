package vdi.service.rest.conversion;

import vdi.core.plugin.registry.PluginRegistry;
import vdi.model.meta.DatasetType;
import vdi.service.rest.generated.model.DatasetTypeOutputImpl;

public class DatasetTypeOutput extends DatasetTypeOutputImpl {
  public DatasetTypeOutput(DatasetType type) {
    setName(type.getNameAsString());
    setVersion(type.getVersion());
    setCategory(PluginRegistry.require(type).getCategory());
  }
}
