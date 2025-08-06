package vdi.service.rest.generated.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import vdi.service.rest.generated.model.HealthResponse;
import vdi.service.rest.generated.model.ServerError;
import vdi.service.rest.generated.support.ResponseDelegate;

@Path("/health")
public interface Health {
  String ROOT_PATH = "/health";

  @GET
  @Produces("application/json")
  GetHealthResponse getHealth();

  class GetHealthResponse extends ResponseDelegate {
    public GetHealthResponse(Response response, Object entity) {
      super(response, entity);
    }

    public GetHealthResponse(Response response) {
      super(response);
    }

    public GetHealthResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static GetHealthResponse respond200WithApplicationJson(HealthResponse entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetHealthResponse(responseBuilder.build(), entity);
    }

    public static GetHealthResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetHealthResponse(responseBuilder.build(), entity);
    }
  }
}
