package org.veupathdb.service.vdi.generated.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Response;
import org.veupathdb.service.vdi.generated.model.BadRequestError;
import org.veupathdb.service.vdi.generated.model.ServerError;
import org.veupathdb.service.vdi.generated.model.ShareOfferEntry;
import org.veupathdb.service.vdi.generated.model.UnauthorizedError;
import org.veupathdb.service.vdi.generated.support.ResponseDelegate;

import java.util.List;

@Path("/vdi-datasets/share-offers")
public interface VdiDatasetsShareOffers {
  @GET
  @Produces("application/json")
  GetVdiDatasetsShareOffersResponse getVdiDatasetsShareOffers(
      @QueryParam("status") @DefaultValue("open") String status);

  class GetVdiDatasetsShareOffersResponse extends ResponseDelegate {
    private GetVdiDatasetsShareOffersResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetVdiDatasetsShareOffersResponse(Response response) {
      super(response);
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
