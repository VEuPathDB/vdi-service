package vdi.service.rest.util;

import java.text.DecimalFormat;

public class Formatting {
  private static final DecimalFormat SizeFormat = new DecimalFormat("#.#");

  private static final long GiB = 1073741824L;
  private static final long MiB = 1048576L;
  private static final long KiB = 1024L;

  public static String formatFileSize(long size) {
    if (size >= GiB)
      return SizeFormat.format((double) size/GiB) + "GiB";
    if (size >= MiB)
      return SizeFormat.format((double) size/MiB) + "MiB";
    if (size >= KiB)
      return SizeFormat.format((double) size/KiB) + "KiB";

    return size + "B";
  }
}
