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

@Path("/vdi-datasets/{vd-id}/files")
public interface VdiDatasetsVdIdFiles {
  @GET
  @Produces("application/json")
  GetVdiDatasetsFilesByVdIdResponse getVdiDatasetsFilesByVdId(@PathParam("vd-id") String vdId);

  @GET
  @Path("/upload")
  @Produces({
      "application/octet-stream",
      "application/json"
  })
  GetVdiDatasetsFilesUploadByVdIdResponse getVdiDatasetsFilesUploadByVdId(
      @PathParam("vd-id") String vdId);

  @GET
  @Path("/data")
  @Produces({
      "application/octet-stream",
      "application/json"
  })
  GetVdiDatasetsFilesDataByVdIdResponse getVdiDatasetsFilesDataByVdId(
      @PathParam("vd-id") String vdId);

  class GetVdiDatasetsFilesByVdIdResponse extends ResponseDelegate {
    private GetVdiDatasetsFilesByVdIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetVdiDatasetsFilesByVdIdResponse(Response response) {
      super(response);
    }

    public static GetVdiDatasetsFilesByVdIdResponse respond200WithApplicationJson(
        DatasetFileListing entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesByVdIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesByVdIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesByVdIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesByVdIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesByVdIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesByVdIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesByVdIdResponse(responseBuilder.build(), entity);
    }
  }

  class GetVdiDatasetsFilesUploadByVdIdResponse extends ResponseDelegate {
    private GetVdiDatasetsFilesUploadByVdIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetVdiDatasetsFilesUploadByVdIdResponse(Response response) {
      super(response);
    }

    public static HeadersFor200 headersFor200() {
      return new HeadersFor200();
    }

    public static GetVdiDatasetsFilesUploadByVdIdResponse respond200WithApplicationOctetStream(
        StreamingOutput entity, HeadersFor200 headers) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/octet-stream");
      responseBuilder.entity(entity);
      headers.toResponseBuilder(responseBuilder);
      return new GetVdiDatasetsFilesUploadByVdIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesUploadByVdIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesUploadByVdIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesUploadByVdIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesUploadByVdIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesUploadByVdIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesUploadByVdIdResponse(responseBuilder.build(), entity);
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

  class GetVdiDatasetsFilesDataByVdIdResponse extends ResponseDelegate {
    private GetVdiDatasetsFilesDataByVdIdResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetVdiDatasetsFilesDataByVdIdResponse(Response response) {
      super(response);
    }

    public static HeadersFor200 headersFor200() {
      return new HeadersFor200();
    }

    public static GetVdiDatasetsFilesDataByVdIdResponse respond200WithApplicationOctetStream(
        StreamingOutput entity, HeadersFor200 headers) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/octet-stream");
      responseBuilder.entity(entity);
      headers.toResponseBuilder(responseBuilder);
      return new GetVdiDatasetsFilesDataByVdIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesDataByVdIdResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesDataByVdIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesDataByVdIdResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesDataByVdIdResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesDataByVdIdResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesDataByVdIdResponse(responseBuilder.build(), entity);
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
