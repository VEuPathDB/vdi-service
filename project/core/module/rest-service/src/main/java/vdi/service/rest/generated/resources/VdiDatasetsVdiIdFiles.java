package vdi.service.rest.generated.resources;

import java.io.File;
import jakarta.ws.rs.core.StreamingOutput;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import vdi.service.rest.generated.model.BadRequestError;
import vdi.service.rest.generated.model.DatasetFileListing;
import vdi.service.rest.generated.model.NotFoundError;
import vdi.service.rest.generated.model.ServerError;
import vdi.service.rest.generated.model.UnauthorizedError;
import vdi.service.rest.generated.support.ResponseDelegate;

@Path("/vdi-datasets/{vdi-id}/files")
public interface VdiDatasetsVdiIdFiles {
  String ROOT_PATH = "/vdi-datasets/{vdi-id}/files";

  String UPLOAD_PATH = ROOT_PATH + "/upload";

  String DATA_PATH = ROOT_PATH + "/data";

  String DOCUMENTS_FILE_NAME_PATH = ROOT_PATH + "/documents/{file-name}";

  String VDI_ID_VAR = "{vdi-id}";

  String FILE_NAME_VAR = "{file-name}";

  @GET
  @Produces("application/json")
  GetVdiDatasetsFilesByVdiIdResponse getVdiDatasetsFilesByVdiId(@PathParam("vdi-id") String vdiId);

  @GET
  @Path("/upload")
  @Produces({
      "application/octet-stream",
      "application/json"
  })
  GetVdiDatasetsFilesUploadByVdiIdResponse getVdiDatasetsFilesUploadByVdiId(
      @PathParam("vdi-id") String vdiId);

  @GET
  @Path("/data")
  @Produces({
      "application/octet-stream",
      "application/json"
  })
  GetVdiDatasetsFilesDataByVdiIdResponse getVdiDatasetsFilesDataByVdiId(
      @PathParam("vdi-id") String vdiId);

  @GET
  @Path("/documents/{file-name}")
  @Produces({
      "application/octet-stream",
      "application/json"
  })
  GetVdiDatasetsFilesDocumentsByVdiIdAndFileNameResponse getVdiDatasetsFilesDocumentsByVdiIdAndFileName(
      @PathParam("vdi-id") String vdiId, @PathParam("file-name") String fileName);

  @PUT
  @Path("/documents/{file-name}")
  @Produces("application/json")
  @Consumes("application/octet-stream")
  PutVdiDatasetsFilesDocumentsByVdiIdAndFileNameResponse putVdiDatasetsFilesDocumentsByVdiIdAndFileName(
      @PathParam("vdi-id") String vdiId, @PathParam("file-name") String fileName, File entity);

  class GetVdiDatasetsFilesByVdiIdResponse extends ResponseDelegate {
    public GetVdiDatasetsFilesByVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    public GetVdiDatasetsFilesByVdiIdResponse(Response response) {
      super(response);
    }

    public GetVdiDatasetsFilesByVdiIdResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static GetVdiDatasetsFilesByVdiIdResponse respond200WithApplicationJson(
        DatasetFileListing entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesByVdiIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesByVdiIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesByVdiIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesByVdiIdResponse(responseBuilder.build(), entity);
    }
  }

  class GetVdiDatasetsFilesUploadByVdiIdResponse extends ResponseDelegate {
    public GetVdiDatasetsFilesUploadByVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    public GetVdiDatasetsFilesUploadByVdiIdResponse(Response response) {
      super(response);
    }

    public GetVdiDatasetsFilesUploadByVdiIdResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static HeadersFor200 headersFor200() {
      return new HeadersFor200();
    }

    public static GetVdiDatasetsFilesUploadByVdiIdResponse respond200WithApplicationOctetStream(
        StreamingOutput entity, HeadersFor200 headers) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/octet-stream");
      responseBuilder.entity(entity);
      headers.toResponseBuilder(responseBuilder);
      return new GetVdiDatasetsFilesUploadByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesUploadByVdiIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesUploadByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesUploadByVdiIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesUploadByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesUploadByVdiIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesUploadByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static class HeadersFor200 extends HeaderBuilderBase {
      private HeadersFor200() {
      }

      public HeadersFor200 withContentDisposition(final String p) {
        headerMap.put("Content-Disposition", String.valueOf(p));;
        return this;
      }
    }
  }

  class GetVdiDatasetsFilesDataByVdiIdResponse extends ResponseDelegate {
    public GetVdiDatasetsFilesDataByVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    public GetVdiDatasetsFilesDataByVdiIdResponse(Response response) {
      super(response);
    }

    public GetVdiDatasetsFilesDataByVdiIdResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static HeadersFor200 headersFor200() {
      return new HeadersFor200();
    }

    public static GetVdiDatasetsFilesDataByVdiIdResponse respond200WithApplicationOctetStream(
        StreamingOutput entity, HeadersFor200 headers) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/octet-stream");
      responseBuilder.entity(entity);
      headers.toResponseBuilder(responseBuilder);
      return new GetVdiDatasetsFilesDataByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesDataByVdiIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesDataByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesDataByVdiIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesDataByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesDataByVdiIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesDataByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static class HeadersFor200 extends HeaderBuilderBase {
      private HeadersFor200() {
      }

      public HeadersFor200 withContentDisposition(final String p) {
        headerMap.put("Content-Disposition", String.valueOf(p));;
        return this;
      }
    }
  }

  class GetVdiDatasetsFilesDocumentsByVdiIdAndFileNameResponse extends ResponseDelegate {
    public GetVdiDatasetsFilesDocumentsByVdiIdAndFileNameResponse(Response response,
        Object entity) {
      super(response, entity);
    }

    public GetVdiDatasetsFilesDocumentsByVdiIdAndFileNameResponse(Response response) {
      super(response);
    }

    public GetVdiDatasetsFilesDocumentsByVdiIdAndFileNameResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static HeadersFor200 headersFor200() {
      return new HeadersFor200();
    }

    public static GetVdiDatasetsFilesDocumentsByVdiIdAndFileNameResponse respond200WithApplicationOctetStream(
        StreamingOutput entity, HeadersFor200 headers) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/octet-stream");
      responseBuilder.entity(entity);
      headers.toResponseBuilder(responseBuilder);
      return new GetVdiDatasetsFilesDocumentsByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesDocumentsByVdiIdAndFileNameResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesDocumentsByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesDocumentsByVdiIdAndFileNameResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesDocumentsByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesDocumentsByVdiIdAndFileNameResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesDocumentsByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static class HeadersFor200 extends HeaderBuilderBase {
      private HeadersFor200() {
      }

      public HeadersFor200 withContentDisposition(final String p) {
        headerMap.put("Content-Disposition", String.valueOf(p));;
        return this;
      }
    }
  }

  class PutVdiDatasetsFilesDocumentsByVdiIdAndFileNameResponse extends ResponseDelegate {
    public PutVdiDatasetsFilesDocumentsByVdiIdAndFileNameResponse(Response response,
        Object entity) {
      super(response, entity);
    }

    public PutVdiDatasetsFilesDocumentsByVdiIdAndFileNameResponse(Response response) {
      super(response);
    }

    public PutVdiDatasetsFilesDocumentsByVdiIdAndFileNameResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static PutVdiDatasetsFilesDocumentsByVdiIdAndFileNameResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new PutVdiDatasetsFilesDocumentsByVdiIdAndFileNameResponse(responseBuilder.build());
    }

    public static PutVdiDatasetsFilesDocumentsByVdiIdAndFileNameResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsFilesDocumentsByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsFilesDocumentsByVdiIdAndFileNameResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsFilesDocumentsByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsFilesDocumentsByVdiIdAndFileNameResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsFilesDocumentsByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static PutVdiDatasetsFilesDocumentsByVdiIdAndFileNameResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutVdiDatasetsFilesDocumentsByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
    }
  }
}
