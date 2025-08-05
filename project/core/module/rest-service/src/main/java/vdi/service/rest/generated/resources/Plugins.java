package vdi.service.rest.generated.resources;

import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import vdi.service.rest.generated.model.PluginListItem;
import vdi.service.rest.generated.model.ServerError;
import vdi.service.rest.generated.model.UnauthorizedError;
import vdi.service.rest.generated.support.ResponseDelegate;

@Path("/plugins")
public interface Plugins {
  String ROOT_PATH = "/plugins";

  @GET
  @Produces("application/json")
  GetPluginsResponse getPlugins(@QueryParam("project") String project);

  class GetPluginsResponse extends ResponseDelegate {
    private GetPluginsResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetPluginsResponse(Response response) {
      super(response);
    }

    public static GetPluginsResponse respond200WithApplicationJson(List<PluginListItem> entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      GenericEntity<List<PluginListItem>> wrappedEntity = new GenericEntity<List<PluginListItem>>(entity){};
      responseBuilder.entity(wrappedEntity);
      return new GetPluginsResponse(responseBuilder.build(), wrappedEntity);
    }

    public static GetPluginsResponse respond401WithApplicationJson(UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetPluginsResponse(responseBuilder.build(), entity);
    }

    public static GetPluginsResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetPluginsResponse(responseBuilder.build(), entity);
    }
  }
}
