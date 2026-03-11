package vdi.service.rest.server;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Context;
import org.glassfish.jersey.server.ContainerRequest;
import org.veupathdb.lib.container.jaxrs.server.annotations.Authenticated;
import vdi.core.db.cache.model.DatasetOwnershipFilter;
import vdi.core.db.cache.query.DatasetListQuery;
import vdi.logging.LoggingExt;
import vdi.service.rest.config.UploadConfig;
import vdi.service.rest.generated.model.DatasetPostRequestBody;
import vdi.service.rest.generated.resources.Datasets;
import vdi.service.rest.server.inputs.DatasetPostInputs;
import vdi.service.rest.server.outputs.OutputErrors;
import vdi.service.rest.server.services.dataset.DatasetListService;
import vdi.service.rest.server.services.dataset.DatasetPostService;
import vdi.service.rest.util.ShortID;

@Authenticated()
public class DatasetListController extends AbstractController implements Datasets {
  private final UploadConfig uploadConfig;

  public DatasetListController(
    @Context ContainerRequest request,
    @Context UploadConfig uploadConfig
  ) {
    super(request);
    this.uploadConfig = uploadConfig;
  }

  @Override
  public GetDatasetsResponse getDatasets(String installTarget, String ownership) {
    var parsedOwnership = ownership == null
      ? DatasetOwnershipFilter.ANY
      : DatasetOwnershipFilter.fromStringOrNull(ownership);

    if (parsedOwnership == null)
      throw new BadRequestException("invalid ownership query param value");

    return GetDatasetsResponse.respond200WithApplicationJson(
      DatasetListService.fetchUserDatasetList(
        this,
        new DatasetListQuery(getUserId(), installTarget, parsedOwnership)
      )
    );
  }

  @Override
  public PostDatasetsResponse postDatasets(DatasetPostRequestBody entity) {
    if (entity == null)
      throw new BadRequestException();

    DatasetPostInputs.cleanup(entity);
    var validation = DatasetPostInputs.validate(entity);

    if (validation.isLeft())
      return PostDatasetsResponse.respond400WithApplicationJson(validation.unwrapLeft());

    var validationErrors = validation.unwrapRight();

    if (validationErrors.isNotEmpty())
      return PostDatasetsResponse.respond422WithApplicationJson(OutputErrors.UnprocessableEntityError(validationErrors));

    var datasetId = ShortID.generateDatasetId();

    getLogger().info("issuing dataset id {}", datasetId);
    setLogger(getLogger().copy(new String[]{LoggingExt.PrefixDatasetID + "=" + datasetId}));

    return DatasetPostService.createDataset(this, datasetId, entity, uploadConfig);
  }
}
