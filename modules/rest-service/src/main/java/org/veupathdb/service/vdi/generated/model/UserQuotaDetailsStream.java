
package org.veupathdb.service.vdi.generated.model;

import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.StreamingOutput;

import java.io.IOException;
import java.io.OutputStream;
import java.util.function.Consumer;

public class UserQuotaDetailsStream extends UserQuotaDetailsImpl implements StreamingOutput {

  private final Consumer<OutputStream> _streamer;

  public UserQuotaDetailsStream(Consumer<OutputStream> streamer) {
    _streamer = streamer;
  }

  @Override
  public void write(OutputStream output) throws IOException, WebApplicationException {
    _streamer.accept(output);
  }
}
