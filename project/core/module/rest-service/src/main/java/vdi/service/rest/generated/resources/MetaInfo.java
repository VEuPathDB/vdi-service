package vdi.service.rest.generated.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import vdi.service.rest.generated.model.ServiceMetadataResponseBody;
import vdi.service.rest.generated.support.ResponseDelegate;

@Path("/meta-info")
public interface MetaInfo {
  String ROOT_PATH = "/meta-info";

  @GET
  @Produces("application/json")
  GetMetaInfoResponse getMetaInfo();

  class GetMetaInfoResponse extends ResponseDelegate {
    private GetMetaInfoResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetMetaInfoResponse(Response response) {
      super(response);
    }

    public static GetMetaInfoResponse respond200WithApplicationJson(
        ServiceMetadataResponseBody entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetMetaInfoResponse(responseBuilder.build(), entity);
    }
  }
}
