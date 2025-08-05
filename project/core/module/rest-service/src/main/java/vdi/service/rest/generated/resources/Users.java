package vdi.service.rest.generated.resources;

import java.util.List;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.GenericEntity;
import javax.ws.rs.core.Response;
import vdi.service.rest.generated.model.BadRequestError;
import vdi.service.rest.generated.model.ServerError;
import vdi.service.rest.generated.model.ShareOfferEntry;
import vdi.service.rest.generated.model.UnauthorizedError;
import vdi.service.rest.generated.model.UserMetadata;
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
      @QueryParam("status") @DefaultValue("open") String status);

  class GetUsersSelfMetaResponse extends ResponseDelegate {
    private GetUsersSelfMetaResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetUsersSelfMetaResponse(Response response) {
      super(response);
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
    private GetUsersSelfShareOffersResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetUsersSelfShareOffersResponse(Response response) {
      super(response);
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
