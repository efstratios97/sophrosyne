package sophrosyne.core.userservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.security.SecureRandom;
import java.util.Base64;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import sophrosyne.core.controlpanelservice.dto.ControlPanelDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "sophrosyne_user")
@SecondaryTable(
    name = "sophrosyne_control_panel",
    pkJoinColumns = @PrimaryKeyJoinColumn(name = "id"))
public class UserDTO {

  private static final int BYCRYPT_STRENGTH = 12;
  private static final int SALT_LENGTH = 16;
  private @Id String id;
  private String username;
  private String firstname;
  private String lastname;
  private String email;
  private String role;
  private String password;
  private String token;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "control_panel_id", referencedColumnName = "id")
  @JsonIgnore
  private ControlPanelDTO controlPanelDTO;

  public static String generateUserId() {
    SecureRandom secureRandom = new SecureRandom();
    byte[] salt = new byte[SALT_LENGTH];
    secureRandom.nextBytes(salt);
    return Base64.getEncoder().encodeToString(salt);
  }

  public static String encodePassword(String plainPassword, String userId) {
    return new BCryptPasswordEncoder(
            BYCRYPT_STRENGTH, new SecureRandom(Base64.getDecoder().decode(userId)))
        .encode(plainPassword);
  }

  public boolean authenticatePassword(String passedPassword) {
    String hashedPassword = this.password;
    byte[] salt = Base64.getDecoder().decode(this.id);
    BCryptPasswordEncoder bCryptPasswordEncoder =
        new BCryptPasswordEncoder(BYCRYPT_STRENGTH, new SecureRandom(salt));
    return bCryptPasswordEncoder.matches(passedPassword, hashedPassword);
  }

  public enum ROLES {
    ADMIN,
    USER,
    CLIENT
  }
}
