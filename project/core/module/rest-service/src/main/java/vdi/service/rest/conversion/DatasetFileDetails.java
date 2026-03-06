package vdi.service.rest.conversion;

import org.veupathdb.lib.s3.s34k.objects.S3Object;
import vdi.model.meta.DatasetFileInfo;
import vdi.service.rest.generated.model.DatasetFileDetailsImpl;

public class DatasetFileDetails extends DatasetFileDetailsImpl {
  public DatasetFileDetails(DatasetFileInfo file) {
    setFileName(file.getName());
    setFileSize(file.getSizeAsLong());
  }

  public DatasetFileDetails(S3Object object) {
    setFileName(object.getBaseName());
    setFileSize(object.getSize());
  }
}
