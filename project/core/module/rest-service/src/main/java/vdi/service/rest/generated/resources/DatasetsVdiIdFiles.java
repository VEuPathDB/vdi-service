package vdi.service.rest.generated.resources;

import java.io.File;
import jakarta.ws.rs.core.StreamingOutput;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import vdi.service.rest.generated.model.BadRequestError;
import vdi.service.rest.generated.model.DatasetFileListing;
import vdi.service.rest.generated.model.DatasetsVdiIdFilesFileNameFileName;
import vdi.service.rest.generated.model.ForbiddenError;
import vdi.service.rest.generated.model.GoneError;
import vdi.service.rest.generated.model.NotFoundError;
import vdi.service.rest.generated.model.ServerError;
import vdi.service.rest.generated.model.UnauthorizedError;
import vdi.service.rest.generated.support.ResponseDelegate;

@Path("/datasets/{vdi-id}/files")
public interface DatasetsVdiIdFiles {
  String ROOT_PATH = "/datasets/{vdi-id}/files";

  String UPLOAD_PATH = ROOT_PATH + "/upload";

  String INSTALL_PATH = ROOT_PATH + "/install";

  String FILE_NAME_PATH = ROOT_PATH + "/{file-name}";

  String DOCUMENTS_FILE_NAME_PATH = ROOT_PATH + "/documents/{file-name}";

  String VARIABLE_PROPERTIES_FILE_NAME_PATH = ROOT_PATH + "/variable-properties/{file-name}";

  String VDI_ID_VAR = "{vdi-id}";

  String FILE_NAME_VAR = "{file-name}";

  @GET
  @Produces("application/json")
  GetDatasetsFilesByVdiIdResponse getDatasetsFilesByVdiId(@PathParam("vdi-id") String vdiId);

  @GET
  @Path("/upload")
  @Produces({
      "application/json",
      "application/zip"
  })
  GetDatasetsFilesUploadByVdiIdResponse getDatasetsFilesUploadByVdiId(
      @PathParam("vdi-id") String vdiId);

  @GET
  @Path("/install")
  @Produces({
      "application/json",
      "application/zip"
  })
  GetDatasetsFilesInstallByVdiIdResponse getDatasetsFilesInstallByVdiId(
      @PathParam("vdi-id") String vdiId);

  @GET
  @Path("/documents/{file-name}")
  @Produces({
      "application/octet-stream",
      "application/json"
  })
  GetDatasetsFilesDocumentsByVdiIdAndFileNameResponse getDatasetsFilesDocumentsByVdiIdAndFileName(
      @PathParam("vdi-id") String vdiId, @PathParam("file-name") String fileName);

  @PUT
  @Path("/documents/{file-name}")
  @Produces("application/json")
  @Consumes("application/octet-stream")
  PutDatasetsFilesDocumentsByVdiIdAndFileNameResponse putDatasetsFilesDocumentsByVdiIdAndFileName(
      @PathParam("vdi-id") String vdiId, @PathParam("file-name") String fileName, File entity);

  @GET
  @Path("/variable-properties/{file-name}")
  @Produces({
      "text/tab-separated-values",
      "application/json"
  })
  GetDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse getDatasetsFilesVariablePropertiesByVdiIdAndFileName(
      @PathParam("vdi-id") String vdiId, @PathParam("file-name") String fileName,
      @QueryParam("download") @DefaultValue("true") Boolean download);

  @PUT
  @Path("/variable-properties/{file-name}")
  @Produces("application/json")
  @Consumes("text/tab-separated-values")
  PutDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse putDatasetsFilesVariablePropertiesByVdiIdAndFileName(
      @PathParam("vdi-id") String vdiId, @PathParam("file-name") String fileName, File entity);

  @GET
  @Path("/{file-name}")
  @Produces("application/json")
  GetDatasetsFilesByVdiIdAndFileNameResponse getDatasetsFilesByVdiIdAndFileName(
      @PathParam("vdi-id") String vdiId,
      @PathParam("file-name") DatasetsVdiIdFilesFileNameFileName fileName,
      @QueryParam("download") @DefaultValue("true") Boolean download);

  class GetDatasetsFilesByVdiIdResponse extends ResponseDelegate {
    public GetDatasetsFilesByVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    public GetDatasetsFilesByVdiIdResponse(Response response) {
      super(response);
    }

    public GetDatasetsFilesByVdiIdResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static GetDatasetsFilesByVdiIdResponse respond200WithApplicationJson(
        DatasetFileListing entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesByVdiIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesByVdiIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesByVdiIdResponse respond410WithApplicationJson(GoneError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(410).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesByVdiIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesByVdiIdResponse(responseBuilder.build(), entity);
    }
  }

  class GetDatasetsFilesUploadByVdiIdResponse extends ResponseDelegate {
    public GetDatasetsFilesUploadByVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    public GetDatasetsFilesUploadByVdiIdResponse(Response response) {
      super(response);
    }

    public GetDatasetsFilesUploadByVdiIdResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static HeadersFor200 headersFor200() {
      return new HeadersFor200();
    }

    public static GetDatasetsFilesUploadByVdiIdResponse respond200WithApplicationZip(Object entity,
        HeadersFor200 headers) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/zip");
      responseBuilder.entity(entity);
      headers.toResponseBuilder(responseBuilder);
      return new GetDatasetsFilesUploadByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesUploadByVdiIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesUploadByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesUploadByVdiIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesUploadByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesUploadByVdiIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesUploadByVdiIdResponse(responseBuilder.build(), entity);
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

  class GetDatasetsFilesInstallByVdiIdResponse extends ResponseDelegate {
    public GetDatasetsFilesInstallByVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    public GetDatasetsFilesInstallByVdiIdResponse(Response response) {
      super(response);
    }

    public GetDatasetsFilesInstallByVdiIdResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static HeadersFor200 headersFor200() {
      return new HeadersFor200();
    }

    public static GetDatasetsFilesInstallByVdiIdResponse respond200WithApplicationZip(Object entity,
        HeadersFor200 headers) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/zip");
      responseBuilder.entity(entity);
      headers.toResponseBuilder(responseBuilder);
      return new GetDatasetsFilesInstallByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesInstallByVdiIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesInstallByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesInstallByVdiIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesInstallByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesInstallByVdiIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesInstallByVdiIdResponse(responseBuilder.build(), entity);
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

  class GetDatasetsFilesDocumentsByVdiIdAndFileNameResponse extends ResponseDelegate {
    public GetDatasetsFilesDocumentsByVdiIdAndFileNameResponse(Response response, Object entity) {
      super(response, entity);
    }

    public GetDatasetsFilesDocumentsByVdiIdAndFileNameResponse(Response response) {
      super(response);
    }

    public GetDatasetsFilesDocumentsByVdiIdAndFileNameResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static HeadersFor200 headersFor200() {
      return new HeadersFor200();
    }

    public static GetDatasetsFilesDocumentsByVdiIdAndFileNameResponse respond200WithApplicationOctetStream(
        StreamingOutput entity, HeadersFor200 headers) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/octet-stream");
      responseBuilder.entity(entity);
      headers.toResponseBuilder(responseBuilder);
      return new GetDatasetsFilesDocumentsByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesDocumentsByVdiIdAndFileNameResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesDocumentsByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesDocumentsByVdiIdAndFileNameResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesDocumentsByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesDocumentsByVdiIdAndFileNameResponse respond410WithApplicationJson(
        GoneError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(410).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesDocumentsByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesDocumentsByVdiIdAndFileNameResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesDocumentsByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
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

  class PutDatasetsFilesDocumentsByVdiIdAndFileNameResponse extends ResponseDelegate {
    public PutDatasetsFilesDocumentsByVdiIdAndFileNameResponse(Response response, Object entity) {
      super(response, entity);
    }

    public PutDatasetsFilesDocumentsByVdiIdAndFileNameResponse(Response response) {
      super(response);
    }

    public PutDatasetsFilesDocumentsByVdiIdAndFileNameResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static PutDatasetsFilesDocumentsByVdiIdAndFileNameResponse respond204() {
      Response.ResponseBuilder responseBuilder = Response.status(204);
      return new PutDatasetsFilesDocumentsByVdiIdAndFileNameResponse(responseBuilder.build());
    }

    public static PutDatasetsFilesDocumentsByVdiIdAndFileNameResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsFilesDocumentsByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static PutDatasetsFilesDocumentsByVdiIdAndFileNameResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsFilesDocumentsByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static PutDatasetsFilesDocumentsByVdiIdAndFileNameResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsFilesDocumentsByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static PutDatasetsFilesDocumentsByVdiIdAndFileNameResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsFilesDocumentsByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
    }
  }

  class PutDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse extends ResponseDelegate {
    public PutDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse(Response response,
        Object entity) {
      super(response, entity);
    }

    public PutDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse(Response response) {
      super(response);
    }

    public PutDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static PutDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse respond202() {
      Response.ResponseBuilder responseBuilder = Response.status(202);
      return new PutDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse(responseBuilder.build());
    }

    public static PutDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse respond400WithApplicationJson(
        BadRequestError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(400).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static PutDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static PutDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse respond403WithApplicationJson(
        ForbiddenError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(403).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static PutDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static PutDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse respond410WithApplicationJson(
        GoneError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(410).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static PutDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new PutDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
    }
  }

  class GetDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse extends ResponseDelegate {
    public GetDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse(Response response,
        Object entity) {
      super(response, entity);
    }

    public GetDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse(Response response) {
      super(response);
    }

    public GetDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static HeadersFor200 headersFor200() {
      return new HeadersFor200();
    }

    public static GetDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse respond200WithTextTabSeparatedValues(
        Object entity, HeadersFor200 headers) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "text/tab-separated-values");
      responseBuilder.entity(entity);
      headers.toResponseBuilder(responseBuilder);
      return new GetDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse respond410WithApplicationJson(
        GoneError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(410).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesVariablePropertiesByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
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

  class GetDatasetsFilesByVdiIdAndFileNameResponse extends ResponseDelegate {
    public GetDatasetsFilesByVdiIdAndFileNameResponse(Response response, Object entity) {
      super(response, entity);
    }

    public GetDatasetsFilesByVdiIdAndFileNameResponse(Response response) {
      super(response);
    }

    public GetDatasetsFilesByVdiIdAndFileNameResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static HeadersFor200 headersFor200() {
      return new HeadersFor200();
    }

    public static GetDatasetsFilesByVdiIdAndFileNameResponse respond200WithApplicationJson(
        Object entity, HeadersFor200 headers) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      headers.toResponseBuilder(responseBuilder);
      return new GetDatasetsFilesByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesByVdiIdAndFileNameResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesByVdiIdAndFileNameResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesByVdiIdAndFileNameResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesByVdiIdAndFileNameResponse(responseBuilder.build(), entity);
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
}
