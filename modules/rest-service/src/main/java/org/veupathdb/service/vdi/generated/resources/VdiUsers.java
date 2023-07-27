package org.veupathdb.service.vdi.generated.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.veupathdb.service.vdi.generated.model.ServerError;
import org.veupathdb.service.vdi.generated.model.UnauthorizedError;
import org.veupathdb.service.vdi.generated.model.UserMetadata;
import org.veupathdb.service.vdi.generated.support.ResponseDelegate;

@Path("/vdi-users")
public interface VdiUsers {
  @GET
  @Path("/self/meta")
  @Produces("application/json")
  GetVdiUsersSelfMetaResponse getVdiUsersSelfMeta();

  class GetVdiUsersSelfMetaResponse extends ResponseDelegate {
    private GetVdiUsersSelfMetaResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetVdiUsersSelfMetaResponse(Response response) {
      super(response);
    }

    public static GetVdiUsersSelfMetaResponse respond200WithApplicationJson(UserMetadata entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiUsersSelfMetaResponse(responseBuilder.build(), entity);
    }

    public static GetVdiUsersSelfMetaResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiUsersSelfMetaResponse(responseBuilder.build(), entity);
    }

    public static GetVdiUsersSelfMetaResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiUsersSelfMetaResponse(responseBuilder.build(), entity);
    }
  }
}
