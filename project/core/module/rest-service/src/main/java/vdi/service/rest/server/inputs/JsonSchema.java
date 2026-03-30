package vdi.service.rest.server.inputs;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.networknt.schema.Schema;
import org.veupathdb.lib.request.validation.ValidationErrors;

public final class JsonSchema {
  public static void validate(ObjectNode value, Schema schema, ValidationErrors errors) {
    var result = schema.validate(value, ctx -> ctx.executionConfig(it -> it.formatAssertionsEnabled(true)));

    if (result.isEmpty())
      return;

    for (var error : result) {
      var location = error.getInstanceLocation().toString();
      var path = location.replaceAll("\\$|^/|^#/", "");

      if (error.getProperty() != null) {
        path = location.isEmpty() ? error.getProperty() : String.format("%s.%s", path, error.getProperty());
      }

      errors.add(path, error.getMessage());
    }
  }
}
