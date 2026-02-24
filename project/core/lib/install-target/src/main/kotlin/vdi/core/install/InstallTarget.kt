package vdi.core.install

import com.networknt.schema.Schema
import vdi.model.meta.DatasetType

data class InstallTarget(
  val enabled:       Boolean,
  val name:          String,
  val dataTypes:     Set<DatasetType>,
  val metaValidation: Schema?,
)
