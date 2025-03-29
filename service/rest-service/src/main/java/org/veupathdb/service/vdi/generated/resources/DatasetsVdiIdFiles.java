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
  GetDatasetsFilesByVdIdAndVdiIdResponse getDatasetsFilesByVdIdAndVdiId(
      @PathParam("vd-id") String vdId, @PathParam("vdi-id") String vdiId);

  @GET
  @Path("/upload")
  @Produces({
      "application/octet-stream",
      "application/json"
  })
  GetDatasetsFilesUploadByVdIdAndVdiIdResponse getDatasetsFilesUploadByVdIdAndVdiId(
      @PathParam("vd-id") String vdId, @PathParam("vdi-id") String vdiId);

  @GET
  @Path("/data")
  @Produces({
      "application/octet-stream",
      "application/json"
  })
  GetDatasetsFilesDataByVdIdAndVdiIdResponse getDatasetsFilesDataByVdIdAndVdiId(
      @PathParam("vd-id") String vdId, @PathParam("vdi-id") String vdiId);

  class GetDatasetsFilesByVdIdAndVdiIdResponse extends ResponseDelegate {
    private GetDatasetsFilesByVdIdAndVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetDatasetsFilesByVdIdAndVdiIdResponse(Response response) {
      super(response);
    }

    public static GetDatasetsFilesByVdIdAndVdiIdResponse respond200WithApplicationJson(
        DatasetFileListing entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesByVdIdAndVdiIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesByVdIdAndVdiIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesByVdIdAndVdiIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }
  }

  class GetDatasetsFilesUploadByVdIdAndVdiIdResponse extends ResponseDelegate {
    private GetDatasetsFilesUploadByVdIdAndVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetDatasetsFilesUploadByVdIdAndVdiIdResponse(Response response) {
      super(response);
    }

    public static HeadersFor200 headersFor200() {
      return new HeadersFor200();
    }

    public static GetDatasetsFilesUploadByVdIdAndVdiIdResponse respond200WithApplicationOctetStream(
        StreamingOutput entity, HeadersFor200 headers) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/octet-stream");
      responseBuilder.entity(entity);
      headers.toResponseBuilder(responseBuilder);
      return new GetDatasetsFilesUploadByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesUploadByVdIdAndVdiIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesUploadByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesUploadByVdIdAndVdiIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesUploadByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesUploadByVdIdAndVdiIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesUploadByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
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

  class GetDatasetsFilesDataByVdIdAndVdiIdResponse extends ResponseDelegate {
    private GetDatasetsFilesDataByVdIdAndVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetDatasetsFilesDataByVdIdAndVdiIdResponse(Response response) {
      super(response);
    }

    public static HeadersFor200 headersFor200() {
      return new HeadersFor200();
    }

    public static GetDatasetsFilesDataByVdIdAndVdiIdResponse respond200WithApplicationOctetStream(
        StreamingOutput entity, HeadersFor200 headers) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/octet-stream");
      responseBuilder.entity(entity);
      headers.toResponseBuilder(responseBuilder);
      return new GetDatasetsFilesDataByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesDataByVdIdAndVdiIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesDataByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesDataByVdIdAndVdiIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesDataByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetDatasetsFilesDataByVdIdAndVdiIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetDatasetsFilesDataByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
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
