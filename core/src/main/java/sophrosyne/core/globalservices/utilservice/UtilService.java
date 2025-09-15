package sophrosyne.core.globalservices.utilservice;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
public class UtilService {

  private final Logger logger = LogManager.getLogger(getClass());

  public Optional<byte[]> readFile(String filePath) {
    byte[] fileBytes = new byte[] {};
    try {
      fileBytes = readFileHelper(new FileInputStream(filePath));
    } catch (FileNotFoundException ex) {
      return Optional.empty();
    }
    return Optional.of(fileBytes);
  }

  private byte[] readFileHelper(FileInputStream fileInputStream) {
    byte[] fileBytes = new byte[] {};
    try (FileInputStream fis = fileInputStream;
        ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
      // Read from the FileInputStream into a buffer
      byte[] buffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = fis.read(buffer)) != -1) {
        // Write the buffer to the ByteArrayOutputStream
        bos.write(buffer, 0, bytesRead);
      }
      fileBytes = bos.toByteArray();
    } catch (IOException e) {
      logger.error(e.getMessage());
    }
    return fileBytes;
  }

  public String convertFileDataToString(byte[] fileData) {
    return new String(fileData, StandardCharsets.UTF_8);
  }
}
