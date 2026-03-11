package vdi.service.rest.services;

import jakarta.ws.rs.InternalServerErrorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vdi.service.rest.s3.DatasetStore;

public final class AdminRpcService {
  private static final Logger LOGGER = LoggerFactory.getLogger(AdminRpcService.class);

  public static void purgeDataset(long userId, String datasetId) {
    try {
      for (var obj : DatasetStore.listDatasetObjects(userId, datasetId))
        obj.delete();
    } catch (Throwable e) {
      LOGGER.error("object deletion failed in dataset {}/{}", userId, datasetId, e);
      throw new InternalServerErrorException("failed to purge dataset");
    }
  }
}
