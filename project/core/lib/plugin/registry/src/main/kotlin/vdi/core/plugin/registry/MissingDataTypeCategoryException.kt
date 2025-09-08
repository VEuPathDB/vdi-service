package vdi.core.plugin.registry

import vdi.model.data.DatasetType

class MissingDataTypeCategoryException(val dataType: DatasetType)
  : Exception("no category was found in the service plugin registry for dataset type $dataType")