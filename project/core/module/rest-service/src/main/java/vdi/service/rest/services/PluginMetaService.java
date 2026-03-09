package vdi.service.rest.services;

import vdi.core.plugin.registry.PluginRegistry;
import vdi.service.rest.conversion.OutputTypes;
import vdi.service.rest.generated.model.PluginListItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.StreamSupport;

public final class PluginMetaService {
  public static List<PluginListItem> listPlugins(String project) {
    var stream = StreamSupport.stream(PluginRegistry.INSTANCE.spliterator(), false);

    if (project != null)
      stream = stream.filter(it -> it.getSecond().appliesTo(project));

    var plugins = new HashMap<String, PluginListItem>(16);

    stream.forEach(it -> {
      var dataType = it.getFirst();
      var plugin = it.getSecond();

      plugins.compute(plugin.getPlugin(), (_, val) -> {
        if (val == null) {
          return OutputTypes.PluginListItem(dataType, plugin);
        } else {
          val.getDataTypes().add(OutputTypes.PluginDataType(dataType));
          return val;
        }
      });
    });

    return new ArrayList<>(plugins.values());
  }
}
