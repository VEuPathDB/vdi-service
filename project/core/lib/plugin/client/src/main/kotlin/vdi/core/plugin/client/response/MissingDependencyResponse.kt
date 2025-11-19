package vdi.core.plugin.client.response

import vdi.io.plugin.responses.MissingDependencyResponse

data class MissingDependencyResponse(override val body: MissingDependencyResponse): DataErrorResponse, InstallDataResponse