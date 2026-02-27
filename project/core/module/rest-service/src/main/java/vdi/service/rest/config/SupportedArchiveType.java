package vdi.service.rest.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum SupportedArchiveType {
  ZIP(".zip"),
  TAR_GZ(".tgz", ".tar.gz");

  private final String[] fileExtensions;

  SupportedArchiveType(String... fileExtensions) {
    this.fileExtensions = fileExtensions;
  }

  public String[] getFileExtensions() {
    return fileExtensions;
  }

  public boolean matches(String filename) {
    for (String ext : fileExtensions) {
      if (filename.endsWith(ext))
        return true;
    }

    return false;
  }

  public static List<String> getAllSupportedExtensions() {
    var out = new ArrayList<String>(3);

    for (var entry : SupportedArchiveType.values())
      out.addAll(Arrays.asList(entry.fileExtensions));

    return out;
  }
}
