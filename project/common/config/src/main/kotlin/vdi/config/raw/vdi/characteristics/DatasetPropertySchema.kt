package vdi.config.raw.vdi.characteristics

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.node.ObjectNode

@JsonDeserialize(using = DatasetPropertySchemaDeserializer::class)
sealed interface DatasetPropertySchema {
  fun resolve(): ObjectNode
}
