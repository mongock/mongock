package io.mongock.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public final class FileUtil {
  private final static Logger logger = LoggerFactory.getLogger(FileUtil.class);

  private FileUtil() {
  }

  public static List<String> readLinesFromFile(String fileName) {
    ClassLoader classLoader = FileUtil.class.getClassLoader();
    InputStream is = classLoader.getResourceAsStream(fileName);
    if (is == null) {
      logger.warn("file not found! " + fileName);
      return new ArrayList<>();
    }
    try (InputStreamReader streamReader = new InputStreamReader(is, StandardCharsets.UTF_8);
         BufferedReader reader = new BufferedReader(streamReader)) {

      List<String> lines = new ArrayList<>();
      String line;
      while ((line = reader.readLine()) != null) {
        lines.add(line);
      }

      return lines;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
