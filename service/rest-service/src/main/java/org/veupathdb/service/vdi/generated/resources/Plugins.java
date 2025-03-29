package org.veupathdb.service.vdi.generated.resources;

import java.util.List;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Response;
import org.veupathdb.service.vdi.generated.model.PluginListItem;
import org.veupathdb.service.vdi.generated.model.ServerError;
import org.veupathdb.service.vdi.generated.model.UnauthorizedError;
import org.veupathdb.service.vdi.generated.support.ResponseDelegate;

@Path("/plugins")
public interface Plugins {
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
