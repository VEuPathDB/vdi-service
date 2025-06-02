package vdi.config.raw.vdi.characteristics

import vdi.config.raw.common.NullSchema

internal data object NullPropertySchema : DatasetPropertySchema {
  override fun resolve() = NullSchema
}
