package vdi.service.rest.conversion;

import vdi.model.meta.DatasetFileInfo;
import vdi.service.rest.generated.model.DatasetZipDetailsImpl;

import java.util.List;

public class DatasetZipDetails extends DatasetZipDetailsImpl {
  public DatasetZipDetails(long zipSize, List<DatasetFileInfo> files) {
    setZipSize(zipSize);
    setContents(files.stream()
      .map(DatasetFileDetails::new)
      .map(vdi.service.rest.generated.model.DatasetFileDetails.class::cast)
      .toList());
  }
}
