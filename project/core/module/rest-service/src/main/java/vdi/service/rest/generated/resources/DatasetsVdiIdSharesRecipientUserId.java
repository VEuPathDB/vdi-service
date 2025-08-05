package vdi.service.rest.generated.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import vdi.service.rest.generated.model.BadRequestError;
import vdi.service.rest.generated.model.DatasetShareOffer;
import vdi.service.rest.generated.model.DatasetShareReceipt;
import vdi.service.rest.generated.model.ForbiddenError;
import vdi.service.rest.generated.model.NotFoundError;
import vdi.service.rest.generated.model.ServerError;
import vdi.service.rest.generated.model.UnauthorizedError;
import vdi.service.rest.generated.model.UnprocessableEntityError;
import vdi.service.rest.generated.support.ResponseDelegate;

@Path("/datasets/{vdi-id}/shares/{recipient-user-id}")
public interface DatasetsVdiIdSharesRecipientUserId {
  String ROOT_PATH = "/datasets/{vdi-id}/shares/{recipient-user-id}";

  String RECEIPT_PATH = ROOT_PATH + "/receipt";

  String OFFER_PATH = ROOT_PATH + "/offer";

  String RECIPIENT_USER_ID_VAR = "{recipient-user-id}";

  String VDI_ID_VAR = "{vdi-id}";

  @PUT
  @Path("/offer")
  @Produces("application/json")
  @Consumes("application/json")
  PutDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse putDatasetsSharesOfferByVdiIdAndRecipientUserId(
      @PathParam("vdi-id") String vdiId, @PathParam("recipient-user-id") long recipientUserId,
      DatasetShareOffer entity);

  @PUT
  @Path("/receipt")
  @Produces("application/json")
  @Consumes("application/json")
  PutDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse putDatasetsSharesReceiptByVdiIdAndRecipientUserId(
      @PathParam("vdi-id") String vdiId, @PathParam("recipient-user-id") long recipientUserId,
      DatasetShareReceipt entity);

  class PutDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse extends ResponseDelegate {
    private PutDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse(Response response,
        Object entity) {
      super(response, entity);
    }

    private PutDatasetsSharesOfferByVdiIdAndRecipientUserIdResponse(Response response) {
      super(response);
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
    private PutDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse(Response response,
        Object entity) {
      super(response, entity);
    }

    private PutDatasetsSharesReceiptByVdiIdAndRecipientUserIdResponse(Response response) {
      super(response);
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
