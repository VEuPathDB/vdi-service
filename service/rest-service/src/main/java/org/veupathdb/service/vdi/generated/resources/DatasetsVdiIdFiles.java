package org.veupathdb.service.vdi.generated.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.StreamingOutput;
import org.veupathdb.service.vdi.generated.model.DatasetFileListing;
import org.veupathdb.service.vdi.generated.model.NotFoundError;
import org.veupathdb.service.vdi.generated.model.ServerError;
import org.veupathdb.service.vdi.generated.model.UnauthorizedError;
import org.veupathdb.service.vdi.generated.support.ResponseDelegate;

@Path("/datasets/{vdi-id}/files")
public interface DatasetsVdiIdFiles {
  @GET
  @Produces("application/json")
  GetDatasetsFilesByVdiIdResponse getDatasetsFilesByVdiId(@PathParam("vdi-id") String vdiId);

  @GET
  @Path("/upload")
  @Produces({
      "application/octet-stream",
      "application/json"
  })
  GetDatasetsFilesUploadByVdiIdResponse getDatasetsFilesUploadByVdiId(
      @PathParam("vdi-id") String vdiId);

  @GET
  @Path("/data")
  @Produces({
      "application/octet-stream",
      "application/json"
  })
  GetDatasetsFilesDataByVdiIdResponse getDatasetsFilesDataByVdiId(
      @PathParam("vdi-id") String vdiId);

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

    public static GetDatasetsFilesUploadByVdiIdResponse respond200WithApplicationOctetStream(
        StreamingOutput entity, HeadersFor200 headers) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/octet-stream");
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
        headerMap.put("Content-Disposition", String.valueOf(p));
        return this;
      }
    }
  }

  class GetDatasetsFilesDataByVdiIdResponse extends ResponseDelegate {
    public GetDatasetsFilesDataByVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    public GetDatasetsFilesDataByVdiIdResponse(Response response) {
      super(response);
    }

    public GetDatasetsFilesDataByVdiIdResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static HeadersFor200 headersFor200() {
      return new HeadersFor200();
    }

    public static GetDatasetsFilesDataByVdiIdResponse respond200WithApplicationOctetStream(
        StreamingOutput entity, HeadersFor200 headers) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/octet-stream");
      responseBuilder.entity(entity);
      headers.toResponseBuilder(responseBuilder);
      return new GetDatasetsFilesDataByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesDataByVdiIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesDataByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesDataByVdiIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesDataByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesDataByVdiIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesDataByVdiIdResponse(responseBuilder.build(), entity);
    }

    public static class HeadersFor200 extends HeaderBuilderBase {
      private HeadersFor200() {
      }

      public HeadersFor200 withContentDisposition(final String p) {
        headerMap.put("Content-Disposition", String.valueOf(p));
        return this;
      }
    }
  }
}
