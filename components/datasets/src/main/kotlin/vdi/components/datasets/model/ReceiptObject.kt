package vdi.components.datasets.model

import com.fasterxml.jackson.annotation.JsonProperty
import vdi.components.datasets.DatasetShare

data class ReceiptObject(@JsonProperty("state") val state: DatasetShare.ReceiptState)