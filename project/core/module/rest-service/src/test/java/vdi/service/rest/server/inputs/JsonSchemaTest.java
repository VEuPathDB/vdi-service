package vdi.service.rest.server.inputs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.IntNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;
import com.networknt.schema.SchemaRegistry;
import com.networknt.schema.SpecificationVersion;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.veupathdb.lib.request.validation.ValidationErrors;

import static org.junit.jupiter.api.Assertions.*;
import static vdi.json.JSONKt.getJSON;

@DisplayName("JsonSchema")
class JsonSchemaTest {
  private static final SchemaRegistry schemaRegistry = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12);

  @Test
  @DisplayName("validation errors are reported")
  public void validateTest1() throws JsonProcessingException {
    // language=json
    var schemaString = """
    {
      "$schema": "https://json-schema.org/draft/2020-12/schema",
      "type": "object",
      "properties": {
        "key": {
          "type": "string",
          "minLength": 3,
          "maxLength": 5
        }
      },
      "required": [ "key" ]
    }
    """;

    var schema = schemaRegistry.getSchema(getJSON().readTree(schemaString));

    var errors = new ValidationErrors();
    var object = getJSON().createObjectNode().<ObjectNode>set("key", new TextNode("hello"));
    JsonSchema.validate(object, schema, errors);
    assertTrue(errors.isEmpty());

    // too short
    errors = new ValidationErrors();
    object = getJSON().createObjectNode().set("key", new TextNode("1"));
    JsonSchema.validate(object, schema, errors);
    assertFalse(errors.isEmpty());

    // too long
    errors = new ValidationErrors();
    object = getJSON().createObjectNode().set("key", new TextNode("123456"));
    JsonSchema.validate(object, schema, errors);
    assertFalse(errors.isEmpty());
  }

  @Test
  @DisplayName("validation error keys are correctly formatted")
  public void validateTest2() throws JsonProcessingException {
    // language=json
    var schemaString = """
    {
      "$schema": "https://json-schema.org/draft/2020-12/schema",
      "type": "object",
      "properties": {
        "level1": {
          "type": "object",
          "properties": {
            "level2": {
              "type": "string",
              "minLength": 3,
              "maxLength": 5
            }
          }
        }
      }
    }
    """;

    var schema = schemaRegistry.getSchema(getJSON().readTree(schemaString));

    var errors = new ValidationErrors();
    var object = getJSON().createObjectNode().<ObjectNode>set("level1", getJSON().createObjectNode().set("level2", new IntNode(3)));
    JsonSchema.validate(object, schema, errors);
    assertFalse(errors.isEmpty());
    assertEquals("level1.level2", errors.getByKey().keySet().stream().findFirst().orElseThrow());
  }
}