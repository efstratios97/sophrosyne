package sophrosyne.core.globalservices.utilservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

@Service
public class UtilService {

    private final Logger logger = LogManager.getLogger(getClass());

    public byte[] readFile(String filePath) {
        byte[] fileBytes = new byte[]{};
        try {
            fileBytes = readFileHelper(new FileInputStream(filePath));
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        return fileBytes;
    }

    public byte[] readFile(File file) {

        byte[] fileBytes = new byte[]{};
        try {
            fileBytes = readFileHelper(new FileInputStream(file));
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        return fileBytes;
    }

    private byte[] readFileHelper(FileInputStream fileInputStream) {
        byte[] fileBytes = new byte[]{};
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

    public void writeMapToFile(Map<String, List<String>> map, String fileName) {

        ObjectMapper objectMapper = new ObjectMapper();
        try (RandomAccessFile file = new RandomAccessFile(fileName, "rw")) {
            // Check if the file is already open
            FileChannel channel = file.getChannel();
            while (channel == null) {
                channel = file.getChannel();
            }
            try (BufferedWriter writer =
                         new BufferedWriter(
                                 new OutputStreamWriter(new FileOutputStream(file.getFD()), StandardCharsets.UTF_8))) {
                objectMapper.writerWithDefaultPrettyPrinter().writeValue(writer, map);
            }
        } catch (IOException e) {
            logger.error(e.getMessage());

        }
    }

    public String convertFileDataToString(byte[] fileData) {
        return new String(fileData, StandardCharsets.UTF_8);
    }
}
