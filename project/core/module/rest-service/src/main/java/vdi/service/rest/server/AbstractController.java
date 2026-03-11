package vdi.service.rest.server;

import org.glassfish.jersey.server.ContainerRequest;
import org.slf4j.LoggerFactory;
import org.veupathdb.lib.container.jaxrs.model.UserInfo;
import org.veupathdb.lib.container.jaxrs.providers.RequestIdProvider;
import org.veupathdb.lib.container.jaxrs.providers.UserProvider;
import vdi.logging.LoggingExt;
import vdi.logging.MarkedLogger;
import vdi.model.meta.UserID;

import java.net.URI;

import static org.gusdb.fgputil.functional.Functions.wrapException;

public abstract class AbstractController {
  private static URI serviceUri;

  public final ContainerRequest request;

  private MarkedLogger logger;

  private UserID actualUserId;

  public AbstractController(ContainerRequest request) {
    this.request = request;

    logger = new MarkedLogger(new String[]{
      LoggingExt.PrefixRequestID + "=" + RequestIdProvider.getRequestId(request),
      LoggingExt.PrefixRequestURI + "=" + request.getUriInfo().getPath(),
    }, LoggerFactory.getLogger(getClass()));
  }

  public MarkedLogger getLogger() {
    return logger;
  }

  protected void setLogger(MarkedLogger logger) {
    this.logger = logger;
  }

  public String getRequestId() {
    return RequestIdProvider.getRequestId(request);
  }

  /**
   * Base URI used to reach this service.  This value may or may not have a path
   * following the hostname.
   * <p>
   * <b>Example</b>
   * <pre>
   * https://foo.something.com/vdi
   * </pre>
   */
  public URI getServiceUri() {
    if (serviceUri == null) {
      var uri = request.getRequestUri();
      var uriPath = request.getUriInfo().getPath();
      serviceUri = wrapException(() -> new URI(
        uri.getScheme(),
        uri.getHost(),
        uri.getPath().substring(0, uriPath.indexOf(request.getUriInfo().getPath()))
      ));
    }

    return serviceUri;
  }

  public UserInfo getUser() {
    return UserProvider.lookupUser(request).orElse(null);
  }

  public UserInfo requireUser() {
    return UserProvider.lookupUser(request).orElseThrow();
  }

  public UserID getUserId() {
    if (actualUserId == null)
      actualUserId = UserID.newUserID(requireUser().getUserId());
    return actualUserId;
  }

  protected void setUserId(UserID userId) {
    actualUserId = userId;
  }



  public String createUrl(String path) {
    return getServiceUri().toString() + path;
  }

  public String redirectUrl(String id) {
    return request.getAbsolutePath().resolve(id).toString();
  }
}
