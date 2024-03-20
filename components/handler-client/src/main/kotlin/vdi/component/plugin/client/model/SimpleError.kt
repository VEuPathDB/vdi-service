package vdi.component.plugin.client.model

import com.fasterxml.jackson.annotation.JsonProperty

data class SimpleError(@JsonProperty(vdi.component.plugin.client.FieldName.Message) var message: String)

