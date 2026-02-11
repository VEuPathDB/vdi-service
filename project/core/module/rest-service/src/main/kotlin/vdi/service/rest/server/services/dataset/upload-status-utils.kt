package vdi.service.rest.server.services.dataset

import org.slf4j.Logger
import vdi.model.meta.DatasetID
import vdi.model.meta.UserID
import vdi.service.rest.generated.model.DatasetUploadStatusCode

context(logger: Logger)
internal fun computeDatasetUploadStatus(userID: UserID, datasetID: DatasetID): DatasetUploadStatusCode {

}