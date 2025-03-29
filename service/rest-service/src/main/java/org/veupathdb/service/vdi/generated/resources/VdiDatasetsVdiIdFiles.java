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

@Path("/vdi-datasets/{vdi-id}/files")
public interface VdiDatasetsVdiIdFiles {
  @GET
  @Produces("application/json")
  GetVdiDatasetsFilesByVdIdAndVdiIdResponse getVdiDatasetsFilesByVdIdAndVdiId(
      @PathParam("vd-id") String vdId, @PathParam("vdi-id") String vdiId);

  @GET
  @Path("/upload")
  @Produces({
      "application/octet-stream",
      "application/json"
  })
  GetVdiDatasetsFilesUploadByVdIdAndVdiIdResponse getVdiDatasetsFilesUploadByVdIdAndVdiId(
      @PathParam("vd-id") String vdId, @PathParam("vdi-id") String vdiId);

  @GET
  @Path("/data")
  @Produces({
      "application/octet-stream",
      "application/json"
  })
  GetVdiDatasetsFilesDataByVdIdAndVdiIdResponse getVdiDatasetsFilesDataByVdIdAndVdiId(
      @PathParam("vd-id") String vdId, @PathParam("vdi-id") String vdiId);

  class GetVdiDatasetsFilesByVdIdAndVdiIdResponse extends ResponseDelegate {
    private GetVdiDatasetsFilesByVdIdAndVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetVdiDatasetsFilesByVdIdAndVdiIdResponse(Response response) {
      super(response);
    }

    public static GetVdiDatasetsFilesByVdIdAndVdiIdResponse respond200WithApplicationJson(
        DatasetFileListing entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesByVdIdAndVdiIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesByVdIdAndVdiIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesByVdIdAndVdiIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }
  }

  class GetVdiDatasetsFilesUploadByVdIdAndVdiIdResponse extends ResponseDelegate {
    private GetVdiDatasetsFilesUploadByVdIdAndVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetVdiDatasetsFilesUploadByVdIdAndVdiIdResponse(Response response) {
      super(response);
    }

    public static HeadersFor200 headersFor200() {
      return new HeadersFor200();
    }

    public static GetVdiDatasetsFilesUploadByVdIdAndVdiIdResponse respond200WithApplicationOctetStream(
        StreamingOutput entity, HeadersFor200 headers) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/octet-stream");
      responseBuilder.entity(entity);
      headers.toResponseBuilder(responseBuilder);
      return new GetVdiDatasetsFilesUploadByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesUploadByVdIdAndVdiIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesUploadByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesUploadByVdIdAndVdiIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesUploadByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesUploadByVdIdAndVdiIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesUploadByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
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

  class GetVdiDatasetsFilesDataByVdIdAndVdiIdResponse extends ResponseDelegate {
    private GetVdiDatasetsFilesDataByVdIdAndVdiIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetVdiDatasetsFilesDataByVdIdAndVdiIdResponse(Response response) {
      super(response);
    }

    public static HeadersFor200 headersFor200() {
      return new HeadersFor200();
    }

    public static GetVdiDatasetsFilesDataByVdIdAndVdiIdResponse respond200WithApplicationOctetStream(
        StreamingOutput entity, HeadersFor200 headers) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/octet-stream");
      responseBuilder.entity(entity);
      headers.toResponseBuilder(responseBuilder);
      return new GetVdiDatasetsFilesDataByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesDataByVdIdAndVdiIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesDataByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesDataByVdIdAndVdiIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesDataByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesDataByVdIdAndVdiIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesDataByVdIdAndVdiIdResponse(responseBuilder.build(), entity);
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
