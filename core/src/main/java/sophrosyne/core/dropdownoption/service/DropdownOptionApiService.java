package sophrosyne.core.dropdownoption.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import sophrosyne.core.dropdownoption.dto.DropdownOptionDTO;

@Service
public class DropdownOptionApiService {

  public DropdownOptionDTO getDropdownOptions(DropdownOptionDTO dropdownOptionDTO)
      throws InterruptedException, IllegalArgumentException, IOException {
    HttpClient client = HttpClient.newHttpClient();
    try {
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create(dropdownOptionDTO.getGetterDropdownOptionCallAddress()))
              .GET()
              .build();

      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

      ObjectMapper mapper = new ObjectMapper();

      List<String> parameters =
          mapper.readValue(response.body(), new TypeReference<List<String>>() {});

      dropdownOptionDTO.setDropdownOptions(parameters);
    } catch (Exception e) {
      List<String> parameters = new ArrayList<>();
      dropdownOptionDTO.setDropdownOptions(parameters);
    }
    return dropdownOptionDTO;
  }
}
