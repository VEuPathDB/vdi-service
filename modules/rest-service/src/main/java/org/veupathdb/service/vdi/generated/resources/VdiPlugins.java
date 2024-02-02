package org.veupathdb.service.vdi.generated.resources;

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

import java.util.List;

@Path("/vdi-plugins")
public interface VdiPlugins {
  @GET
  @Produces("application/json")
  GetVdiPluginsResponse getVdiPlugins(@QueryParam("project") String project);

  class GetVdiPluginsResponse extends ResponseDelegate {
    private GetVdiPluginsResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetVdiPluginsResponse(Response response) {
      super(response);
    }

    public static GetVdiPluginsResponse respond200WithApplicationJson(List<PluginListItem> entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      GenericEntity<List<PluginListItem>> wrappedEntity = new GenericEntity<List<PluginListItem>>(entity){};
      responseBuilder.entity(wrappedEntity);
      return new GetVdiPluginsResponse(responseBuilder.build(), wrappedEntity);
    }

    public static GetVdiPluginsResponse respond401WithApplicationJson(UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiPluginsResponse(responseBuilder.build(), entity);
    }

    public static GetVdiPluginsResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiPluginsResponse(responseBuilder.build(), entity);
    }
  }
}
