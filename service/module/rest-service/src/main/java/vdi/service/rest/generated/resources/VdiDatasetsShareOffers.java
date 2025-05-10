package vdi.service.rest.generated.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Response;
import vdi.service.rest.generated.model.BadRequestError;
import vdi.service.rest.generated.model.ServerError;
import vdi.service.rest.generated.model.ShareOfferEntry;
import vdi.service.rest.generated.model.UnauthorizedError;
import vdi.service.rest.generated.support.ResponseDelegate;

import java.util.List;

@Path("/vdi-datasets/share-offers")
public interface VdiDatasetsShareOffers {
  String ROOT_PATH = "/vdi-datasets/share-offers";

  @GET
  @Produces("application/json")
  GetVdiDatasetsShareOffersResponse getVdiDatasetsShareOffers(
      @QueryParam("status") @DefaultValue("open") String status);

  class GetVdiDatasetsShareOffersResponse extends ResponseDelegate {
    public GetVdiDatasetsShareOffersResponse(Response response, Object entity) {
      super(response, entity);
    }

    public GetVdiDatasetsShareOffersResponse(Response response) {
      super(response);
    }

    public GetVdiDatasetsShareOffersResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static GetVdiDatasetsShareOffersResponse respond200WithApplicationJson(
        List<ShareOfferEntry> entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      GenericEntity<List<ShareOfferEntry>> wrappedEntity = new GenericEntity<List<ShareOfferEntry>>(entity){};
      responseBuilder.entity(wrappedEntity);
      return new GetVdiDatasetsShareOffersResponse(responseBuilder.build(), wrappedEntity);
    }

    public static GetVdiDatasetsShareOffersResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsShareOffersResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsShareOffersResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsShareOffersResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsShareOffersResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsShareOffersResponse(responseBuilder.build(), entity);
    }
  }
}
