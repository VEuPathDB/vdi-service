package vdi.lib.config.vdi

import vdi.lib.config.common.NullSchema

internal data object NullPropertySchema : DatasetPropertySchema {
  override fun resolve() = NullSchema
}
