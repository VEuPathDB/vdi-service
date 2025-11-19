package vdi.core.plugin.registry

import vdi.model.meta.DatasetType

class MissingDataTypeMetaException(val dataType: DatasetType)
  : Exception("no metadata was found in the service plugin registry for dataset type $dataType")