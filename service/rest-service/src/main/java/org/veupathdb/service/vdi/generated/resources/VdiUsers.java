package org.veupathdb.service.vdi.generated.resources;

import java.util.List;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Response;
import org.veupathdb.service.vdi.generated.model.BadRequestError;
import org.veupathdb.service.vdi.generated.model.ServerError;
import org.veupathdb.service.vdi.generated.model.ShareOfferEntry;
import org.veupathdb.service.vdi.generated.model.UnauthorizedError;
import org.veupathdb.service.vdi.generated.model.UserMetadata;
import org.veupathdb.service.vdi.generated.support.ResponseDelegate;

@Path("/vdi-users")
public interface VdiUsers {
  @GET
  @Path("/self/meta")
  @Produces("application/json")
  GetVdiUsersSelfMetaResponse getVdiUsersSelfMeta();

  @GET
  @Path("/self/share-offers")
  @Produces("application/json")
  GetVdiUsersSelfShareOffersResponse getVdiUsersSelfShareOffers(
      @QueryParam("status") @DefaultValue("open") String status);

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

  class GetVdiUsersSelfShareOffersResponse extends ResponseDelegate {
    private GetVdiUsersSelfShareOffersResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetVdiUsersSelfShareOffersResponse(Response response) {
      super(response);
    }

    public static GetVdiUsersSelfShareOffersResponse respond200WithApplicationJson(
        List<ShareOfferEntry> entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      GenericEntity<List<ShareOfferEntry>> wrappedEntity = new GenericEntity<List<ShareOfferEntry>>(entity){};
      responseBuilder.entity(wrappedEntity);
      return new GetVdiUsersSelfShareOffersResponse(responseBuilder.build(), wrappedEntity);
    }

    public static GetVdiUsersSelfShareOffersResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiUsersSelfShareOffersResponse(responseBuilder.build(), entity);
    }

    public static GetVdiUsersSelfShareOffersResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiUsersSelfShareOffersResponse(responseBuilder.build(), entity);
    }

    public static GetVdiUsersSelfShareOffersResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiUsersSelfShareOffersResponse(responseBuilder.build(), entity);
    }
  }
}
