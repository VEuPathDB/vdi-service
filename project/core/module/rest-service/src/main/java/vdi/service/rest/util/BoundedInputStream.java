package vdi.service.rest.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.function.Supplier;

public class BoundedInputStream extends InputStream {
  private final long maxBytes;

  private final InputStream inputStream;

  private final Supplier<RuntimeException> exceptionProvider;

  private long bytesRead;

  public BoundedInputStream(long maxBytes, InputStream inputStream, Supplier<RuntimeException> exceptionProvider) {
    this.maxBytes = maxBytes;
    this.inputStream = inputStream;
    this.exceptionProvider = exceptionProvider;
  }

  @Override
  public int read() throws IOException {
    if (++bytesRead > maxBytes)
      throw exceptionProvider.get();

    return inputStream.read();
  }

  @Override
  public int read(byte[] b) throws IOException {
    var red = inputStream.read(b);

    bytesRead += red;

    if (bytesRead > maxBytes)
      throw exceptionProvider.get();

    return red;
  }

  @Override
  public int read(byte[] b, int off, int len) throws IOException {
    var red = inputStream.read(b, off, len);

    bytesRead += red;
    if (bytesRead > maxBytes)
      throw exceptionProvider.get();

    return red;
  }

  @Override
  public void close() throws IOException {
    inputStream.close();
  }
}
