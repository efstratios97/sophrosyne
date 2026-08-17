package sophrosyne.core.dropdownoption.dto;

import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity(name = "sophrosyne_dropdown_option")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class DropdownOptionDTO {

  @Id private String id;
  private String name;
  private String description;
  private DROPDOWN_OPTION_TYPE type;
  private String dynamicParameterToMatch;
  private String getterDropdownOptionCallAddress;

  @Column(name = "dropdown_options", columnDefinition = "jsonb")
  @JdbcTypeCode(SqlTypes.JSON)
  private List<String> dropdownOptions;

  public enum DROPDOWN_OPTION_TYPE {
    STATIC,
    DYNAMIC
  }
}
