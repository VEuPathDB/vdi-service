package org.veupathdb.vdi.lib.handler.client.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.veupathdb.vdi.lib.handler.client.FieldName

data class SimpleError(@JsonProperty(FieldName.Message) var message: String)

