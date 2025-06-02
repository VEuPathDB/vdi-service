package vdi.model.data

import com.fasterxml.jackson.annotation.JsonGetter
import com.fasterxml.jackson.core.JsonParseException
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.fasterxml.jackson.databind.node.ObjectNode


@JsonDeserialize(using = DatasetFileInfo.FileInfoAdapter::class)
data class DatasetFileInfo(
  @get:JsonGetter(JsonKey.FileName)
  val filename: String,

  @get:JsonGetter(JsonKey.FileSize)
  val size: ULong,
) {
  object JsonKey {
    const val FileName = "filename"
    const val FileSize = "fileSize"
  }

  internal object FileInfoAdapter : JsonDeserializer<DatasetFileInfo>() {
    override fun deserialize(parser: JsonParser, ctx: DeserializationContext): DatasetFileInfo {
      val raw = ctx.readValue(parser, ObjectNode::class.java)

      var name: String? = null
      var size: ULong? = null

      for ((key, value) in raw.fields()) {
        when (key) {
          JsonKey.FileName -> name = value.textValue()
            ?: throw JsonParseException("expected $key to be a string but was ${value.nodeType} instead")
          JsonKey.FileSize,
          "size" -> size = (value.takeIf { it.isNumber }
            ?.asText() ?: throw JsonParseException("expected $key to be a number but was ${value.nodeType} instead"))
            .toULong()
        }
      }

      if (name == null)
        throw JsonParseException("missing required field: ${JsonKey.FileName}")
      if (size == null)
        throw JsonParseException("missing required field: ${JsonKey.FileSize}")

      return DatasetFileInfo(name, size)
    }
  }
}
