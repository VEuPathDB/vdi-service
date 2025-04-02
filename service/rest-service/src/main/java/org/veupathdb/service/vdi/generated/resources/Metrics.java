package org.veupathdb.service.vdi.generated.resources;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.Response;
import org.veupathdb.service.vdi.generated.support.ResponseDelegate;

@Path("/metrics")
public interface Metrics {
  @GET
  @Produces("text/plain")
  GetMetricsResponse getMetrics();

  class GetMetricsResponse extends ResponseDelegate {
    public GetMetricsResponse(Response response, Object entity) {
      super(response, entity);
    }

    public GetMetricsResponse(Response response) {
      super(response);
    }

    public GetMetricsResponse(ResponseDelegate response) {
      super(response.delegate, response.entity);
    }

    public static GetMetricsResponse respond200WithTextPlain(Object entity) {
      Response.ResponseBuilder responseBuilder = Response.status(200).header("Content-Type", "text/plain");
      responseBuilder.entity(entity);
      return new GetMetricsResponse(responseBuilder.build(), entity);
    }
  }
}
