package vdi.model.compat

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import vdi.model.data.DatasetInstallTarget

/**
 * Compatibility layer that upgrades installTarget array entries from strings
 * into install target objects containing build information.
 */
internal class Upgrade_InstallTarget_StringToObj(): JsonDeserializer<DatasetInstallTarget>() {
  override fun deserialize(parser: JsonParser, ctx: DeserializationContext): DatasetInstallTarget =
    if (parser.currentToken.isScalarValue)
      DatasetInstallTarget(targetID = parser.valueAsString, buildInfo = null)
    else
      parser.readValueAs(DatasetInstallTarget::class.java)
}