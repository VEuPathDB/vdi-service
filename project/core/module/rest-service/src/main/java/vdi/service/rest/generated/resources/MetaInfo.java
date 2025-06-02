package vdi.service.rest.generated.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import vdi.service.rest.generated.model.ServiceMetadataResponseBody;
import vdi.service.rest.generated.support.ResponseDelegate;

@Path("/meta-info")
public interface MetaInfo {
  String ROOT_PATH = "/meta-info";

  @GET
  @Produces("application/json")
  GetMetaInfoResponse getMetaInfo();

  class GetMetaInfoResponse extends ResponseDelegate {
    public GetMetaInfoResponse(Response response, Object entity) {
      super(response, entity);
    }

    public GetMetaInfoResponse(Response response) {
      super(response);
    }

    public GetMetaInfoResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static GetMetaInfoResponse respond200WithApplicationJson(
        ServiceMetadataResponseBody entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetMetaInfoResponse(responseBuilder.build(), entity);
    }
  }
}
