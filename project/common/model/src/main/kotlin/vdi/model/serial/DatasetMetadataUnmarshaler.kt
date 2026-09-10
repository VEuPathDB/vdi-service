package vdi.model.serial

import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonMappingException
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.contains
import vdi.model.meta.DatasetContact
import vdi.model.meta.DatasetDependency
import vdi.model.meta.DatasetFundingAward
import vdi.model.meta.DatasetMetadata
import vdi.model.meta.DatasetOrganism
import vdi.model.meta.DatasetPublication
import vdi.model.meta.DatasetSource
import vdi.model.meta.LinkedDataset
import vdi.model.meta.MetadataContentFlags

class DatasetMetadataUnmarshaler: StdDeserializer<DatasetMetadata>(DatasetMetadata::class.java) {
  private companion object {
    private val StringSetType  = object: TypeReference<Set<String>>() {}
    private val Dependencies   = object: TypeReference<List<DatasetDependency>>() {}
    private val Publications   = object: TypeReference<List<DatasetPublication>>() {}
    private val Contacts       = object: TypeReference<List<DatasetContact>>() {}
    private val LinkedDatasets = object: TypeReference<List<LinkedDataset>>() {}
    private val Funding        = object: TypeReference<List<DatasetFundingAward>>() {}
    private val Sources        = object: TypeReference<List<DatasetSource>>() {}
    private val OrgDetails     = object: TypeReference<List<DatasetOrganism>>() {}
  }

  override fun deserialize(p: JsonParser, ctxt: DeserializationContext): DatasetMetadata? {
    val asObj = p.readValueAsTree<ObjectNode>()

    if (asObj == null || asObj.isNull)
      return null

    when (asObj[DatasetMetadata.VDIMetaVersion]?.asInt() ?: 1) {
      1    -> v1To2(asObj, p)
      2    -> { /* current version */ }
      else -> throw JsonMappingException(p, "unrecognized vdi dataset metadata version value")
    }

    return context(ctxt) { DatasetMetadata(
      vdiMetadataVersion     = asObj.require(DatasetMetadata.VDIMetaVersion),
      type                   = asObj.require(DatasetMetadata.Type),
      // can't use the `require` mixin here due to type erasure requiring us
      // construct a TypeReference
      installTargets         = StringSetType.parse(asObj[DatasetMetadata.InstallTargets])
        .require(DatasetMetadata.InstallTargets),
      visibility             = asObj.require(DatasetMetadata.Visibility),
      owner                  = asObj.require(DatasetMetadata.Owner),
      name                   = asObj.require(DatasetMetadata.Name),
      summary                = asObj.require(DatasetMetadata.Summary),
      description            = asObj.nullable(DatasetMetadata.Description),
      origin                 = asObj.require(DatasetMetadata.Origin),
      created                = asObj.require(DatasetMetadata.Created),
      sourceURL              = asObj.nullable(DatasetMetadata.SourceURL),
      dependencies           = Dependencies.parse(asObj[DatasetMetadata.Dependencies])
        ?: emptyList(),
      publications           = Publications.parse(asObj[DatasetMetadata.Publications])
        ?: emptyList(),
      contacts               = Contacts.parse(asObj[DatasetMetadata.Contacts])
        ?: emptyList(),
      shortAttribution       = asObj.nullable(DatasetMetadata.ShortAttribution),
      projectName            = asObj.nullable(DatasetMetadata.ProjectName),
      programName            = asObj.nullable(DatasetMetadata.ProgramName),
      linkedDatasets         = LinkedDatasets.parse(asObj[DatasetMetadata.LinkedDatasets])
        ?: emptyList(),
      hostOrganism           = asObj.nullable(DatasetMetadata.HostOrganism),
      datasetCharacteristics = asObj.nullable(DatasetMetadata.Characteristics),
      externalIdentifiers    = asObj.nullable(DatasetMetadata.ExternalIdentifiers),
      funding                = Funding.parse(asObj[DatasetMetadata.Funding])
        ?: emptyList(),
      revisionHistory        = asObj.nullable(DatasetMetadata.RevisionHistory),
      daysForApproval        = asObj.nullable(DatasetMetadata.DaysForApproval)
        ?: -1,
      dataDisclaimer         = asObj.nullable(DatasetMetadata.DataDisclaimer),
      datasetSources         = Sources.parse(asObj[DatasetMetadata.DatasetSources])
        ?: emptyList(),
      metadataContentFlags   = asObj.nullable(DatasetMetadata.MetadataContentFlags)
        ?: MetadataContentFlags(),
      experimentalOrganisms              = OrgDetails.parse(asObj[DatasetMetadata.ExperimentalOrganisms])
        ?: emptyList(),
    ) }
  }

  // TODO: temporary implementation until we add more migrations or someone has
  //       free time to do cleanup.
  private fun v1To2(json: ObjectNode, p: JsonParser) {
    // set the new version maker
    json.put(DatasetMetadata.VDIMetaVersion, 2)

    // Move experimental organism to the new organism details array if present
    if (DatasetMetadata.LegacyExperimentalOrganism in json) {
      val array = json.get(DatasetMetadata.ExperimentalOrganisms) as? ArrayNode
        ?: json.putArray(DatasetMetadata.ExperimentalOrganisms)

      array.add(json.get(DatasetMetadata.LegacyExperimentalOrganism))
      json.remove(DatasetMetadata.LegacyExperimentalOrganism)
    }
  }

  private fun <T> T?.require(key: String): T =
    this ?: throw JsonParseException("propert \"$key\" is required and must not be null")

  context(ctx: DeserializationContext)
  private inline fun <reified T: Any> ObjectNode.require(key: String): T =
    get(key)?.takeUnless { it.isNull }
      .require(key)
      .let { ctx.readTreeAsValue(it, T::class.java) }

  context(ctx: DeserializationContext)
  private inline fun <reified T: Any> ObjectNode.nullable(key: String): T? =
    get(key)?.takeUnless { it.isNull }
      ?.let { ctx.readTreeAsValue(it, T::class.java) }

  context(ctx: DeserializationContext)
  private fun <T> TypeReference<T>.toType() =
    ctx.typeFactory.constructType(this)!!


  context(ctx: DeserializationContext)
  private fun <T> TypeReference<T>.parse(node: JsonNode?): T? =
    node?.takeUnless(JsonNode::isNull)
      ?.let { ctx.readTreeAsValue(it, toType()) }
}