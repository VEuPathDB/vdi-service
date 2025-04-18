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
}
