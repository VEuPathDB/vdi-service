package vdi.core.plugin.registry

import vdi.model.data.DatasetType

class MissingDataTypeMetaException(val dataType: DatasetType)
  : Exception("no metadata was found in the service plugin registry for dataset type $dataType")