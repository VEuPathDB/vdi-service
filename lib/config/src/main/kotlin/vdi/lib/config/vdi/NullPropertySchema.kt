package vdi.lib.config.vdi

internal data object NullPropertySchema : DatasetPropertySchema {
  override fun resolve() = NullSchema
}
