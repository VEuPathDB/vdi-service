package org.veupathdb.service.vdi.generated.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import org.veupathdb.service.vdi.generated.model.BadRequestError;
import org.veupathdb.service.vdi.generated.model.DatasetShareOffer;
import org.veupathdb.service.vdi.generated.model.DatasetShareReceipt;
import org.veupathdb.service.vdi.generated.model.ForbiddenError;
import org.veupathdb.service.vdi.generated.model.NotFoundError;
import org.veupathdb.service.vdi.generated.model.ServerError;
import org.veupathdb.service.vdi.generated.model.UnauthorizedError;
import org.veupathdb.service.vdi.generated.model.UnprocessableEntityError;
import org.veupathdb.service.vdi.generated.support.ResponseDelegate;

@Path("/vdi-datasets/{vd-id}/shares/{recipient-user-id}")
public interface VdiDatasetsVdIdSharesRecipientUserId {
  @PUT
  @Path("/offer")
  @Produces("application/json")
  @Consumes("application/json")
  PutVdiDatasetsSharesOfferByVdIdAndRecipientUserIdResponse putVdiDatasetsSharesOfferByVdIdAndRecipientUserId(
      @PathParam("vd-id") String vdId, @PathParam("recipient-user-id") long recipientUserId,
      DatasetShareOffer entity);

  @PUT
  @Path("/receipt")
  @Produces("application/json")
  @Consumes("application/json")
  PutVdiDatasetsSharesReceiptByVdIdAndRecipientUserIdResponse putVdiDatasetsSharesReceiptByVdIdAndRecipientUserId(
      @PathParam("vd-id") String vdId, @PathParam("recipient-user-id") long recipientUserId,
      DatasetShareReceipt entity);

  class PutVdiDatasetsSharesOfferByVdIdAndRecipientUserIdResponse extends ResponseDelegate {
    private PutVdiDatasetsSharesOfferByVdIdAndRecipientUserIdResponse(Response response,
        Object entity) {
      super(response, entity);
    }

    private PutVdiDatasetsSharesOfferByVdIdAndRecipientUserIdResponse(Response response) {
      super(response);
    }

    public static PutVdiDatasetsSharesOfferByVdIdAndRecipientUserIdResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new PutVdiDatasetsSharesOfferByVdIdAndRecipientUserIdResponse(responseBuilder.build());
    }

    public static PutVdiDatasetsSharesOfferByVdIdAndRecipientUserIdResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsSharesOfferByVdIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsSharesOfferByVdIdAndRecipientUserIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsSharesOfferByVdIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsSharesOfferByVdIdAndRecipientUserIdResponse respond403WithApplicationJson(
        ForbiddenError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(403).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsSharesOfferByVdIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsSharesOfferByVdIdAndRecipientUserIdResponse respond404() {
      Response.ResponseBuilder responseBuilder = Response.status(404);
      return new PutVdiDatasetsSharesOfferByVdIdAndRecipientUserIdResponse(responseBuilder.build());
    }

    public static PutVdiDatasetsSharesOfferByVdIdAndRecipientUserIdResponse respond422WithApplicationJson(
        UnprocessableEntityError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(422).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsSharesOfferByVdIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsSharesOfferByVdIdAndRecipientUserIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsSharesOfferByVdIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }
  }

  class PutVdiDatasetsSharesReceiptByVdIdAndRecipientUserIdResponse extends ResponseDelegate {
    private PutVdiDatasetsSharesReceiptByVdIdAndRecipientUserIdResponse(Response response,
        Object entity) {
      super(response, entity);
    }

    private PutVdiDatasetsSharesReceiptByVdIdAndRecipientUserIdResponse(Response response) {
      super(response);
    }

    public static PutVdiDatasetsSharesReceiptByVdIdAndRecipientUserIdResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new PutVdiDatasetsSharesReceiptByVdIdAndRecipientUserIdResponse(responseBuilder.build());
    }

    public static PutVdiDatasetsSharesReceiptByVdIdAndRecipientUserIdResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsSharesReceiptByVdIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsSharesReceiptByVdIdAndRecipientUserIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsSharesReceiptByVdIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsSharesReceiptByVdIdAndRecipientUserIdResponse respond403() {
      Response.ResponseBuilder responseBuilder = Response.status(403);
      return new PutVdiDatasetsSharesReceiptByVdIdAndRecipientUserIdResponse(responseBuilder.build());
    }

    public static PutVdiDatasetsSharesReceiptByVdIdAndRecipientUserIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsSharesReceiptByVdIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsSharesReceiptByVdIdAndRecipientUserIdResponse respond422WithApplicationJson(
        UnprocessableEntityError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(422).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsSharesReceiptByVdIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsSharesReceiptByVdIdAndRecipientUserIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsSharesReceiptByVdIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }
  }
}
