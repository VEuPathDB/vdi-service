package org.veupathdb.service.vdi.generated.resources;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import org.veupathdb.service.vdi.generated.model.BadRequestError;
import org.veupathdb.service.vdi.generated.model.DatasetShareOffer;
import org.veupathdb.service.vdi.generated.model.DatasetShareReceipt;
import org.veupathdb.service.vdi.generated.model.ForbiddenError;
import org.veupathdb.service.vdi.generated.model.NotFoundError;
import org.veupathdb.service.vdi.generated.model.ServerError;
import org.veupathdb.service.vdi.generated.model.UnauthorizedError;
import org.veupathdb.service.vdi.generated.model.UnprocessableEntityError;
import org.veupathdb.service.vdi.generated.support.ResponseDelegate;

@Path("/datasets/{vdi-id}/shares/{recipient-user-id}")
public interface DatasetsVdiIdSharesRecipientUserId {
  @PUT
  @Path("/offer")
  @Produces("application/json")
  @Consumes("application/json")
  PutDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse putDatasetsSharesOfferByVdiIdAndRecipientUserId(
      @PathParam("vdi-id") String vdiId, @PathParam("recipient-user-id") Long recipientUserId,
      DatasetShareOffer entity);

  @PUT
  @Path("/receipt")
  @Produces("application/json")
  @Consumes("application/json")
  PutDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse putDatasetsSharesReceiptByVdiIdAndRecipientUserId(
      @PathParam("vdi-id") String vdiId, @PathParam("recipient-user-id") Long recipientUserId,
      DatasetShareReceipt entity);

  class PutDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse extends ResponseDelegate {
    public PutDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse(Response response,
        Object entity) {
      super(response, entity);
    }

    public PutDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse(Response response) {
      super(response);
    }

    public PutDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static PutDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new PutDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse(responseBuilder.build());
    }

    public static PutDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }

    public static PutDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }

    public static PutDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse respond403WithApplicationJson(
        ForbiddenError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(403).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }

    public static PutDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }

    public static PutDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse respond422WithApplicationJson(
        UnprocessableEntityError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(422).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }

    public static PutDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }
  }

  class PutDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse extends ResponseDelegate {
    public PutDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse(Response response,
        Object entity) {
      super(response, entity);
    }

    public PutDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse(Response response) {
      super(response);
    }

    public PutDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static PutDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new PutDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse(responseBuilder.build());
    }

    public static PutDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }

    public static PutDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }

    public static PutDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse respond403WithApplicationJson(
        ForbiddenError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(403).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }

    public static PutDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }

    public static PutDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse respond422WithApplicationJson(
        UnprocessableEntityError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(422).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }

    public static PutDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }
  }
}
