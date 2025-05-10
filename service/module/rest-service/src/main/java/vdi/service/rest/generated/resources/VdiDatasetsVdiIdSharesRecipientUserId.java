package vdi.service.rest.generated.resources;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import vdi.service.rest.generated.model.*;
import vdi.service.rest.generated.support.ResponseDelegate;

@Path("/vdi-datasets/{vdi-id}/shares/{recipient-user-id}")
public interface VdiDatasetsVdiIdSharesRecipientUserId {
  String ROOT_PATH = "/vdi-datasets/{vdi-id}/shares/{recipient-user-id}";

  String RECEIPT_PATH = ROOT_PATH + "/receipt";

  String OFFER_PATH = ROOT_PATH + "/offer";

  String RECIPIENT_USER_ID_VAR = "{recipient-user-id}";

  String VDI_ID_VAR = "{vdi-id}";

  @PUT
  @Path("/offer")
  @Produces("application/json")
  @Consumes("application/json")
  PutVdiDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse putVdiDatasetsSharesOfferByVdiIdAndRecipientUserId(
      @PathParam("vdi-id") String vdiId, @PathParam("recipient-user-id") Long recipientUserId,
      DatasetShareOffer entity);

  @PUT
  @Path("/receipt")
  @Produces("application/json")
  @Consumes("application/json")
  PutVdiDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse putVdiDatasetsSharesReceiptByVdiIdAndRecipientUserId(
      @PathParam("vdi-id") String vdiId, @PathParam("recipient-user-id") Long recipientUserId,
      DatasetShareReceipt entity);

  class PutVdiDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse extends ResponseDelegate {
    public PutVdiDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse(Response response,
        Object entity) {
      super(response, entity);
    }

    public PutVdiDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse(Response response) {
      super(response);
    }

    public PutVdiDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static PutVdiDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new PutVdiDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse(responseBuilder.build());
    }

    public static PutVdiDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse respond403WithApplicationJson(
        ForbiddenError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(403).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse respond422WithApplicationJson(
        UnprocessableEntityError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(422).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }
  }

  class PutVdiDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse extends ResponseDelegate {
    public PutVdiDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse(Response response,
        Object entity) {
      super(response, entity);
    }

    public PutVdiDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse(Response response) {
      super(response);
    }

    public PutVdiDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static PutVdiDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new PutVdiDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse(responseBuilder.build());
    }

    public static PutVdiDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse respond403WithApplicationJson(
        ForbiddenError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(403).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse respond422WithApplicationJson(
        UnprocessableEntityError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(422).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse(responseBuilder.build(), entity);
    }
  }
}
