package vdi.components.datasets.model

import com.fasterxml.jackson.annotation.JsonProperty

data class DatasetShareReceipt(@JsonProperty("action") val action: DatasetShareReceiptAction)

