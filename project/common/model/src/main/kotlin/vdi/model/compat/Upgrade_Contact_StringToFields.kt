package vdi.model.compat

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.node.TextNode
import com.fasterxml.jackson.databind.node.ValueNode
import com.fasterxml.jackson.module.kotlin.convertValue
import vdi.json.JSON
import vdi.model.data.DatasetContact

/**
 * Compatibility layer to split single-string name fields into first/middle/last
 * fields.
 */
internal class Upgrade_Contact_StringToFields(): JsonDeserializer<DatasetContact>() {
  override fun deserialize(parser: JsonParser, ctx: DeserializationContext): DatasetContact {
    val tmp = parser.readValueAs<MutableMap<String, ValueNode>>(object : TypeReference<MutableMap<String, ValueNode>>() {})

    if (DatasetContact.Legacy_Name in tmp) {
      val raw = tmp[DatasetContact.Legacy_Name]!!.textValue()
      val d1 = raw.indexOf(' ')
      val d2 = raw.lastIndexOf(' ')

      // Remove the old name field
      tmp.remove(DatasetContact.Legacy_Name)

      // Add the new name fields.
      when (d1) {
        // No space found.
        -1 -> {
          tmp[DatasetContact.FirstName] = raw.asJson()
          tmp[DatasetContact.LastName]  = "".asJson()
        }

        // One space found.  Split to first and last name.
        d2 -> {
          tmp[DatasetContact.FirstName] = raw.substring(0, d1).asJson()
          tmp[DatasetContact.LastName]  = raw.substring(d1+1).asJson()
        }

        // More than one space found.  Everything between the 2 spaces becomes
        // the middle name.
        else -> {
          tmp[DatasetContact.FirstName]  = raw.substring(0, d1).asJson()
          tmp[DatasetContact.MiddleName] = raw.substring(d1+1, d2).asJson()
          tmp[DatasetContact.LastName]   = raw.substring(d2+1).asJson()
        }
      }
    }

    return JSON.convertValue(tmp)
  }

  private fun String.asJson() = TextNode(this)
}