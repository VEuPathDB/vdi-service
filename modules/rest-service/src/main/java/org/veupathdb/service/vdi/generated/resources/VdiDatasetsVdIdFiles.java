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
  @Path("/upload/{file-name}")
  @Produces({
      "application/octet-stream",
      "application/json"
  })
  GetVdiDatasetsFilesUploadByVdIdAndFileNameResponse getVdiDatasetsFilesUploadByVdIdAndFileName(
      @PathParam("vd-id") String vdId, @PathParam("file-name") String fileName);

  @GET
  @Path("/data/{file-name}")
  @Produces({
      "application/octet-stream",
      "application/json"
  })
  GetVdiDatasetsFilesDataByVdIdAndFileNameResponse getVdiDatasetsFilesDataByVdIdAndFileName(
      @PathParam("vd-id") String vdId, @PathParam("file-name") String fileName);

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

  class GetVdiDatasetsFilesUploadByVdIdAndFileNameResponse extends ResponseDelegate {
    private GetVdiDatasetsFilesUploadByVdIdAndFileNameResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetVdiDatasetsFilesUploadByVdIdAndFileNameResponse(Response response) {
      super(response);
    }

    public static GetVdiDatasetsFilesUploadByVdIdAndFileNameResponse respond200WithApplicationOctetStream(
        StreamingOutput entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/octet-stream");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesUploadByVdIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesUploadByVdIdAndFileNameResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesUploadByVdIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesUploadByVdIdAndFileNameResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesUploadByVdIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesUploadByVdIdAndFileNameResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesUploadByVdIdAndFileNameResponse(responseBuilder.build(), entity);
    }
  }

  class GetVdiDatasetsFilesDataByVdIdAndFileNameResponse extends ResponseDelegate {
    private GetVdiDatasetsFilesDataByVdIdAndFileNameResponse(Response response, Object entity) {
      super(response, entity);
    }

    private GetVdiDatasetsFilesDataByVdIdAndFileNameResponse(Response response) {
      super(response);
    }

    public static GetVdiDatasetsFilesDataByVdIdAndFileNameResponse respond200WithApplicationOctetStream(
        StreamingOutput entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "application/octet-stream");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesDataByVdIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesDataByVdIdAndFileNameResponse respond401WithApplicationJson(
        UnauthorizedError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(401).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesDataByVdIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesDataByVdIdAndFileNameResponse respond404WithApplicationJson(
        NotFoundError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(404).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesDataByVdIdAndFileNameResponse(responseBuilder.build(), entity);
    }

    public static GetVdiDatasetsFilesDataByVdIdAndFileNameResponse respond500WithApplicationJson(
        ServerError entity) {
      Response.ResponseBuilder responseBuilder = Response.status(500).header("Content-Type", "application/json");
      responseBuilder.entity(entity);
      return new GetVdiDatasetsFilesDataByVdIdAndFileNameResponse(responseBuilder.build(), entity);
    }
  }
}
