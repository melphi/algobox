package io.algobox.backtest.spark.common.util;

import java.io.File;

import static io.algobox.util.MorePreconditions.checkNotNullOrEmpty;

public final class FileUtil {
  private static final String PREFIX_GOOGLE_STORAGE = "gs:";
  private static final String PREFIX_LOCAL_FOLDER = "/";

  public static boolean isFilePresent(String fileName) {
    checkNotNullOrEmpty(fileName);
    if (fileName.startsWith(PREFIX_GOOGLE_STORAGE)) {
      return isFilePresentGoogleStorage(fileName);
    } else if (fileName.startsWith(PREFIX_LOCAL_FOLDER)) {
      return isFilePresentLocalFolder(fileName);
    } else {
      throw new IllegalArgumentException(
          String.format("File [%s] should start with the prefix gs:/ or /.", fileName));
    }
  }

  private static boolean isFilePresentGoogleStorage(String fileName) {
    throw new IllegalArgumentException("Not yet implemented.");
  }

  private static boolean isFilePresentLocalFolder(String fileName) {
    return new File(fileName).exists();
  }
}
