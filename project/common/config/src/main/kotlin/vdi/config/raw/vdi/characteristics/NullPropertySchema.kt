package vdi.config.raw.vdi.characteristics

internal data object NullPropertySchema : DatasetPropertySchema {
  override fun resolve() = NullSchema
}
