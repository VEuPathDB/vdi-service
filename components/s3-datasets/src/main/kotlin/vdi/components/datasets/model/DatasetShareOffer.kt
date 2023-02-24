package vdi.components.datasets.model

import com.fasterxml.jackson.annotation.JsonProperty

data class DatasetShareOffer(@JsonProperty("action") val action: DatasetShareOfferAction)