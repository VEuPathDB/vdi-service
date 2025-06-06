package vdi.model.data

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.convertValue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.FieldSource
import java.net.URI
import kotlin.test.assertEquals
import vdi.json.JSON
import vdi.json.toJSONString
import vdi.model.OriginTimestamp

@DisplayName("DatasetMetadata")
class DatasetMetadataTest {
  companion object {
    private fun ObjectNode.withField(name: String, value: String) =
      deepCopy().put(name, value)
    private fun ObjectNode.withField(name: String, value: ArrayNode) =
      deepCopy().set<ObjectNode>(name, value)
    private fun ObjectNode.withField(name: String, value: ObjectNode) =
      deepCopy().set<ObjectNode>(name, value)

    private val minimal = JSON.createObjectNode()
      .set<ObjectNode>("type", JSON.createObjectNode().put("name", "hello").put("version", "1.2"))
      .set<ObjectNode>("installTargets", JSON.createArrayNode().add("Something"))
      .put("visibility", "private")
      .put("owner", 1234)
      .put("name", "name")
      .put("summary", "summary")
      .put("origin", "origin")
      .putPOJO("created", OriginTimestamp)

    @JvmStatic
    val tests = listOf(
      Test(
        "minimal representation",
        minimal,
        DatasetMetadata(
          type = DatasetType(DataType.of("hello"), "1.2"),
          installTargets = setOf("Something"),
          visibility = DatasetVisibility.Private,
          owner = UserID(1234),
          name = "name",
          summary = "summary",
          origin = "origin",
          created = OriginTimestamp,
        )
      ),
      Test(
        "with short name",
        minimal.withField("shortName", "short name"),
        DatasetMetadata(
          type = DatasetType(DataType.of("hello"), "1.2"),
          installTargets = setOf("Something"),
          visibility = DatasetVisibility.Private,
          owner = UserID(1234),
          name = "name",
          summary = "summary",
          origin = "origin",
          created = OriginTimestamp,
          shortName = "short name",
        )
      ),
      Test(
        "with description",
        minimal.withField("description", "description"),
        DatasetMetadata(
          type = DatasetType(DataType.of("hello"), "1.2"),
          installTargets = setOf("Something"),
          visibility = DatasetVisibility.Private,
          owner = UserID(1234),
          name = "name",
          summary = "summary",
          origin = "origin",
          created = OriginTimestamp,
          description = "description",
        )
      ),
      Test(
        "with short attribution",
        minimal.withField("shortAttribution", "short attribution"),
        DatasetMetadata(
          type = DatasetType(DataType.of("hello"), "1.2"),
          installTargets = setOf("Something"),
          visibility = DatasetVisibility.Private,
          owner = UserID(1234),
          name = "name",
          summary = "summary",
          origin = "origin",
          created = OriginTimestamp,
          shortAttribution = "short attribution",
        )
      ),
      Test(
        "with source URL",
        minimal.withField("sourceUrl", "https://duckduckgo.com"),
        DatasetMetadata(
          type = DatasetType(DataType.of("hello"), "1.2"),
          installTargets = setOf("Something"),
          visibility = DatasetVisibility.Private,
          owner = UserID(1234),
          name = "name",
          summary = "summary",
          origin = "origin",
          created = OriginTimestamp,
          sourceURL = URI.create("https://duckduckgo.com"),
        )
      ),
      Test(
        "with dependencies",
        minimal.withField("dependencies", JSON.createArrayNode()
          .add(JSON.createObjectNode()
            .put("resourceIdentifier", "resource identifier")
            .put("resourceVersion", "resource version")
            .put("resourceDisplayName", "resource display name"))),
        DatasetMetadata(
          type = DatasetType(DataType.of("hello"), "1.2"),
          installTargets = setOf("Something"),
          visibility = DatasetVisibility.Private,
          owner = UserID(1234),
          name = "name",
          summary = "summary",
          origin = "origin",
          created = OriginTimestamp,
          dependencies = listOf(DatasetDependency(
            "resource identifier",
            "resource version",
            "resource display name",
          )),
        )
      ),
      Test(
        "with publications",
        minimal.withField("publications", JSON.createArrayNode()
          .add(JSON.createObjectNode()
            .put("pubmedId", "something"))),
        DatasetMetadata(
          type = DatasetType(DataType.of("hello"), "1.2"),
          installTargets = setOf("Something"),
          visibility = DatasetVisibility.Private,
          owner = UserID(1234),
          name = "name",
          summary = "summary",
          origin = "origin",
          created = OriginTimestamp,
          publications = listOf(DatasetPublication("something")),
        )
      ),
      Test(
        "with hyperlinks",
        minimal.withField("hyperlinks", JSON.createArrayNode()
          .add(JSON.createObjectNode()
            .put("url", "https://floops.fooglies")
            .put("text", "some text"))),
        DatasetMetadata(
          type = DatasetType(DataType.of("hello"), "1.2"),
          installTargets = setOf("Something"),
          visibility = DatasetVisibility.Private,
          owner = UserID(1234),
          name = "name",
          summary = "summary",
          origin = "origin",
          created = OriginTimestamp,
          hyperlinks = listOf(DatasetHyperlink(URI.create("https://floops.fooglies"), "some text")),
        )
      ),
      Test(
        "with organisms",
        minimal.withField("organisms", JSON.createArrayNode()
          .add("organism")),
        DatasetMetadata(
          type = DatasetType(DataType.of("hello"), "1.2"),
          installTargets = setOf("Something"),
          visibility = DatasetVisibility.Private,
          owner = UserID(1234),
          name = "name",
          summary = "summary",
          origin = "origin",
          created = OriginTimestamp,
          organisms = setOf("organism"),
        )
      ),
      Test(
        "with contacts",
        minimal.withField("contacts", JSON.createArrayNode()
          .add(JSON.createObjectNode()
            .put("name", "a name"))),
        DatasetMetadata(
          type = DatasetType(DataType.of("hello"), "1.2"),
          installTargets = setOf("Something"),
          visibility = DatasetVisibility.Private,
          owner = UserID(1234),
          name = "name",
          summary = "summary",
          origin = "origin",
          created = OriginTimestamp,
          contacts = listOf(DatasetContact("a name")),
        )
      ),
      Test(
        "with original id",
        minimal.withField("originalId", "hello"),
        DatasetMetadata(
          type = DatasetType(DataType.of("hello"), "1.2"),
          installTargets = setOf("Something"),
          visibility = DatasetVisibility.Private,
          owner = UserID(1234),
          name = "name",
          summary = "summary",
          origin = "origin",
          created = OriginTimestamp,
          originalID = DatasetID("hello"),
        )
      ),
      Test(
        "with revision history",
        minimal.withField("revisionHistory", JSON.createArrayNode()
          .add(JSON.createObjectNode()
            .put("action", "revise")
            .putPOJO("timestamp", OriginTimestamp)
            .put("revisionId", "goodbye")
            .put("revisionNote", "some note"))),
        DatasetMetadata(
          type = DatasetType(DataType.of("hello"), "1.2"),
          installTargets = setOf("Something"),
          visibility = DatasetVisibility.Private,
          owner = UserID(1234),
          name = "name",
          summary = "summary",
          origin = "origin",
          created = OriginTimestamp,
          revisionHistory = listOf(DatasetRevision(
            DatasetRevision.Action.Revise,
            OriginTimestamp,
            DatasetID("goodbye"),
            "some note",
          )),
        )
      ),
      Test(
        "with properties",
        minimal.withField("properties", JSON.createObjectNode()
          .put("something", "something else")),
        DatasetMetadata(
          type = DatasetType(DataType.of("hello"), "1.2"),
          installTargets = setOf("Something"),
          visibility = DatasetVisibility.Private,
          owner = UserID(1234),
          name = "name",
          summary = "summary",
          origin = "origin",
          created = OriginTimestamp,
          properties = DatasetProperties(JSON.createObjectNode()
            .put("something", "something else")),
        )
      ),
    )
  }

  @Nested
  @DisplayName("Deserialization")
  inner class Deserialization {
    @ParameterizedTest(name = "{0}")
    @FieldSource("vdi.model.data.DatasetMetadataTest#tests")
    fun test1(test: Test) {
      JSON.convertValue<DatasetMetadata>(test.json)
        .also {
          assertEquals(it, test.value)
        }
    }
  }

  @Nested
  @DisplayName("Serialization")
  inner class Serialization {
    @ParameterizedTest(name = "{0}")
    @FieldSource("vdi.model.data.DatasetMetadataTest#tests")
    fun test1(test: Test) {
      assertEquals(test.json.toJSONString(), test.value.toJSONString())
    }
  }

  data class Test(
    val name: String,
    val json: ObjectNode,
    val value: DatasetMetadata,
  ) {
    override fun toString() = name
  }
}