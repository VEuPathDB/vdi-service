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
import org.veupathdb.service.vdi.generated.support.ResponseDelegate;

@Path("/datasets/share-offers")
public interface DatasetsShareOffers {
  @GET
  @Produces("application/json")
  GetDatasetsShareOffersResponse getDatasetsShareOffers(
      @QueryParam("status") @DefaultValue("open") String status);

  class GetDatasetsShareOffersResponse extends ResponseDelegate {
    private GetDatasetsShareOffersResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetDatasetsShareOffersResponse(Response response) {
      super(response);
    }

    public static GetDatasetsShareOffersResponse respond200WithApplicationJson(
        List<ShareOfferEntry> entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      GenericEntity<List<ShareOfferEntry>> wrappedEntity = new GenericEntity<List<ShareOfferEntry>>(entity){};
      responseBuilder.entity(wrappedEntity);
      return new GetDatasetsShareOffersResponse(responseBuilder.build(), wrappedEntity);
    }

    public static GetDatasetsShareOffersResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsShareOffersResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsShareOffersResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsShareOffersResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsShareOffersResponse respond500WithApplicationJson(ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsShareOffersResponse(responseBuilder.build(), entity);
    }
  }
}
