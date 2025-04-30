package vdi.service.rest.generated.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import vdi.service.rest.generated.support.ResponseDelegate;

@Path("/api")
public interface Api {
  String ROOT_PATH = "/api";

  @GET
  @Produces("text/html")
  GetApiResponse getApi();

  class GetApiResponse extends ResponseDelegate {
    public GetApiResponse(Response response, Object entity) {
      super(response, entity);
    }

    public GetApiResponse(Response response) {
      super(response);
    }

    public GetApiResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static GetApiResponse respond200WithTextHtml(Object entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "text/html");
      responseBuilder.entity(entity);
      return new GetApiResponse(responseBuilder.build(), entity);
    }
  }
}
