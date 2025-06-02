package vdi.config.raw.vdi.characteristics

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import java.net.URI
import kotlin.io.path.Path

internal class DatasetPropertySchemaDeserializer: StdDeserializer<DatasetPropertySchema>(DatasetPropertySchema::class.java) {
  override fun deserialize(p: JsonParser, ctxt: DeserializationContext) =
    if (p.currentToken.isStructStart)
      InlineDatasetPropertySchema(p.codec.readTree(p))
    else
      p.codec.readValue(p, String::class.java).let {
        if (it.startsWith("http"))
          RemoteDatasetPropertySchema(URI.create(it).toURL())
        else
          LocalDatasetPropertySchema(Path(it))
      }
}
