package vdi.service.rest.generated.resources;

import java.util.List;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Response;
import vdi.service.rest.generated.model.BadRequestError;
import vdi.service.rest.generated.model.ServerError;
import vdi.service.rest.generated.model.ShareOfferEntry;
import vdi.service.rest.generated.model.UnauthorizedError;
import vdi.service.rest.generated.model.UserMetadata;
import vdi.service.rest.generated.model.UsersSelfShareOffersGetStatus;
import vdi.service.rest.generated.support.ResponseDelegate;

@Path("/users")
public interface Users {
  String ROOT_PATH = "/users";

  String SELF_META_PATH = ROOT_PATH + "/self/meta";

  String SELF_SHARE_OFFERS_PATH = ROOT_PATH + "/self/share-offers";

  @GET
  @Path("/self/meta")
  @Produces("application/json")
  GetUsersSelfMetaResponse getUsersSelfMeta();

  @GET
  @Path("/self/share-offers")
  @Produces("application/json")
  GetUsersSelfShareOffersResponse getUsersSelfShareOffers(
      @QueryParam("status") @DefaultValue("open") UsersSelfShareOffersGetStatus status);

  class GetUsersSelfMetaResponse extends ResponseDelegate {
    public GetUsersSelfMetaResponse(Response response, Object entity) {
      super(response, entity);
    }

    public GetUsersSelfMetaResponse(Response response) {
      super(response);
    }

    public GetUsersSelfMetaResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static GetUsersSelfMetaResponse respond200WithApplicationJson(UserMetadata entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetUsersSelfMetaResponse(responseBuilder.build(), entity);
    }

    public static GetUsersSelfMetaResponse respond401WithApplicationJson(UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetUsersSelfMetaResponse(responseBuilder.build(), entity);
    }

    public static GetUsersSelfMetaResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetUsersSelfMetaResponse(responseBuilder.build(), entity);
    }
  }

  class GetUsersSelfShareOffersResponse extends ResponseDelegate {
    public GetUsersSelfShareOffersResponse(Response response, Object entity) {
      super(response, entity);
    }

    public GetUsersSelfShareOffersResponse(Response response) {
      super(response);
    }

    public GetUsersSelfShareOffersResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static GetUsersSelfShareOffersResponse respond200WithApplicationJson(
        List<ShareOfferEntry> entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      GenericEntity<List<ShareOfferEntry>> wrappedEntity = new GenericEntity<List<ShareOfferEntry>>(entity){};
      responseBuilder.entity(wrappedEntity);
      return new GetUsersSelfShareOffersResponse(responseBuilder.build(), wrappedEntity);
    }

    public static GetUsersSelfShareOffersResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetUsersSelfShareOffersResponse(responseBuilder.build(), entity);
    }

    public static GetUsersSelfShareOffersResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetUsersSelfShareOffersResponse(responseBuilder.build(), entity);
    }

    public static GetUsersSelfShareOffersResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetUsersSelfShareOffersResponse(responseBuilder.build(), entity);
    }
  }
}
