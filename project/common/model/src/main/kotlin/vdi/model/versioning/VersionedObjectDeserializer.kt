package vdi.model.versioning

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.node.ObjectNode
import vdi.json.JSON
import vdi.model.meta.DatasetFileInfo
import vdi.model.meta.DatasetManifest
import vdi.model.meta.DatasetMetadata
import vdi.model.meta.VersionedMetaObject


internal class VersionedObjectDeserializer: JsonDeserializer<VersionedMetaObject>() {

  @Suppress("UNCHECKED_CAST")
  override fun deserialize(parser: JsonParser, ctx: DeserializationContext): VersionedMetaObject {
    val kind = (ctx.contextualType.rawClass as Class<out VersionedMetaObject>).kotlin

    MetaStructureVersion.CurrentVersion.migrator

    when (kind) {
      DatasetMetadata::class,
      DatasetManifest::class,
      DatasetFileInfo::class -> { /* OK */ }
      else -> throw IllegalStateException("unrecognized versioned meta type: $kind")
    }

    val raw = JSON.readValue(parser, ObjectNode::class.java)

    if (raw.version >= MetaStructureVersion.CurrentVersion)
      return JSON.convertValue(raw, kind.java)

    MetaStructureVersion.entries.forEach { v ->
      if (v > raw.version)
        v.migrator.migrate(raw, kind)
    }

    return JSON.convertValue(raw, kind.java)
  }

  private inline val ObjectNode.version
    get() = get(VersionedMetaObject.VersionKey)
      ?.textValue()
      ?.let { MetaStructureVersion.fromString(it) }
      ?: MetaStructureVersion.v1_0_0
}