package sophrosyne.core.apikeyservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Entity(name = "sophrosyne_apikey")
public class ApikeyDTO {

  private @Id String apikey;
  private String apikeyname;
  private String apikeydescription;
  private int apikeyactive;
  private String privatekey;
  private String publickey;
}
