package vdi.config.raw.vdi.characteristics

import com.fasterxml.jackson.databind.node.ObjectNode

@JvmInline
internal value class InlineDatasetPropertySchema(private val schema: ObjectNode): DatasetPropertySchema {
  override fun resolve() = schema
}
