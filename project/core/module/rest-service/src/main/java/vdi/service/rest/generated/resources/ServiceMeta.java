package vdi.service.rest.generated.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import vdi.service.rest.generated.model.ServerError;
import vdi.service.rest.generated.model.ServiceMetadataResponseBody;
import vdi.service.rest.generated.support.ResponseDelegate;

@Path("/service-meta")
public interface ServiceMeta {
  String ROOT_PATH = "/service-meta";

  @GET
  @Produces("application/json")
  GetServiceMetaResponse getServiceMeta();

  class GetServiceMetaResponse extends ResponseDelegate {
    public GetServiceMetaResponse(Response response, Object entity) {
      super(response, entity);
    }

    public GetServiceMetaResponse(Response response) {
      super(response);
    }

    public GetServiceMetaResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static GetServiceMetaResponse respond200WithApplicationJson(
        ServiceMetadataResponseBody entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetServiceMetaResponse(responseBuilder.build(), entity);
    }

    public static GetServiceMetaResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetServiceMetaResponse(responseBuilder.build(), entity);
    }
  }
}
