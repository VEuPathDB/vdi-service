package vdi.model.serial

import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertContentEquals
import vdi.json.JSON
import vdi.model.meta.DatasetMetadata
import vdi.model.meta.DatasetOrganism
import vdi.model.meta.DatasetType

class DatasetMetadataUnmarshalerTest {

  private fun makeMinimalDatasetJson() = JSON.createObjectNode()
    .apply {
      putObject(DatasetMetadata.Type).also { type ->
        type.put(DatasetType.Name, "something")
        type.put(DatasetType.Version, "nothing")
      }

      putArray(DatasetMetadata.InstallTargets)

      put(DatasetMetadata.Visibility, "private")
      put(DatasetMetadata.Owner, 1)
      put(DatasetMetadata.Name, "name")
      put(DatasetMetadata.Summary, "summary")
      put(DatasetMetadata.Origin, "origin")
      put(DatasetMetadata.Created, "2022-07-01T00:00:00Z")
    }

  @Test
  fun unmarshal_v1_metadata_with_experimentalOrganism() {
    val json = makeMinimalDatasetJson()
      .apply {
        putObject(DatasetMetadata.LegacyExperimentalOrganism)
          .apply{
            put(DatasetOrganism.Species, "species")
            put(DatasetOrganism.Strain, "strain")
          }
      }
      .toString()

    val parsed = assertDoesNotThrow { JSON.readValue<DatasetMetadata>(json) }

    assertContentEquals(
      setOf(DatasetOrganism("species", "strain")),
      parsed.experimentalOrganisms,
    )
  }
}