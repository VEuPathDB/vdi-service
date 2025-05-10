package vdi.service.rest.generated.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Response;
import vdi.service.rest.generated.model.*;
import vdi.service.rest.generated.support.ResponseDelegate;

import java.util.List;

@Path("/vdi-users")
public interface VdiUsers {
  String ROOT_PATH = "/vdi-users";

  String SELF_META_PATH = ROOT_PATH + "/self/meta";

  String SELF_SHARE_OFFERS_PATH = ROOT_PATH + "/self/share-offers";

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
    public GetVdiUsersSelfMetaResponse(Response response, Object entity) {
      super(response, entity);
    }

    public GetVdiUsersSelfMetaResponse(Response response) {
      super(response);
    }

    public GetVdiUsersSelfMetaResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
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
    public GetVdiUsersSelfShareOffersResponse(Response response, Object entity) {
      super(response, entity);
    }

    public GetVdiUsersSelfShareOffersResponse(Response response) {
      super(response);
    }

    public GetVdiUsersSelfShareOffersResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
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
