package sophrosyne.core.dropdownoptionservice;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import sophrosyne.core.PostgresIntegrationTestBase;
import sophrosyne.core.dropdownoption.dto.DropdownOptionDTO;
import sophrosyne.core.dropdownoption.service.DropdownOptionService;
import sophrosyne.core.utils.DropdownOptionUtils;
import sophrosyne_api.core.internaldropdownoptionservice.model.DropdownOption;

@SpringBootTest
@TestPropertySource(locations = "/application-test.properties")
public class DropdownOptionServiceIT extends PostgresIntegrationTestBase {

  @Autowired private DropdownOptionService sut_dropdownOptionService;
  @Autowired private DropdownOptionUtils dropdownOptionUtils;

  @BeforeEach
  @AfterEach
  public void clearDropdownOptions() {
    sut_dropdownOptionService.deleteAllDropdownOptions();
  }

  @Test
  public void test_createDropdownOptionDTOFromDropdownOption() {
    DropdownOption dropdownOption = dropdownOptionUtils.createDropdownOption();
    DropdownOptionDTO dropdownOptionDTO =
        sut_dropdownOptionService.createDropdownOptionDTO(dropdownOption);
    // Object Test
    assertThat(dropdownOptionDTO.getDescription()).isEqualTo(dropdownOption.getDescription());
    assertThat(dropdownOptionDTO.getName()).isEqualTo(dropdownOption.getName());
    assertThat(dropdownOptionDTO.getType().name()).isEqualTo(dropdownOption.getType().getValue());
    assertThat(dropdownOptionDTO.getDropdownOptions())
        .isEqualTo(dropdownOption.getDropdownOptions());
    // Object from DB test
    DropdownOptionDTO sut_dropdownOptionDTO =
        sut_dropdownOptionService.getDropdownOptionDTO(dropdownOptionDTO.getId()).get();
    assertThat(dropdownOptionDTO).isEqualTo(sut_dropdownOptionDTO);
  }

  @Test
  public void test_updateDropdownOptionDTOFromDropdownOption() {
    DropdownOption dropdownOption = dropdownOptionUtils.createDropdownOption();
    DropdownOptionDTO dropdownOptionDTO =
        sut_dropdownOptionService.createDropdownOptionDTO(dropdownOption);
    dropdownOption.setName("new_name");
    dropdownOption.setId(dropdownOptionDTO.getId());
    DropdownOptionDTO updatedDropdownOptionDTO =
        sut_dropdownOptionService.updateDropdownOption(dropdownOption);
    // Object from DB test
    DropdownOptionDTO sut_dropdownOptionDTO =
        sut_dropdownOptionService.getDropdownOptionDTO(dropdownOptionDTO.getId()).get();
    assertThat(updatedDropdownOptionDTO).isEqualTo(sut_dropdownOptionDTO);
  }

  @Test
  public void test_deleteDropdownOptionDTO() {
    List<DropdownOptionDTO> dropdownOptionDTOS = createTwoDropdownOptions();
    assertThat(sut_dropdownOptionService.getDropdownOptionDTOs()).hasSize(2);
    sut_dropdownOptionService.deleteDropdownOption(dropdownOptionDTOS.getFirst().getId());
    sut_dropdownOptionService.deleteDropdownOption(dropdownOptionDTOS.getLast().getId());
    assertThat(sut_dropdownOptionService.getDropdownOptionDTOs()).hasSize(0);
  }

  @Test
  public void test_deleteAllDropdownOptionDTOs() {
    createTwoDropdownOptions();
    assertThat(sut_dropdownOptionService.getDropdownOptionDTOs()).hasSize(2);
    sut_dropdownOptionService.deleteAllDropdownOptions();
    assertThat(sut_dropdownOptionService.getDropdownOptionDTOs()).hasSize(0);
  }

  private List<DropdownOptionDTO> createTwoDropdownOptions() {
    DropdownOption dropdownOption1 = dropdownOptionUtils.createDropdownOption();
    DropdownOptionDTO dropdownOptionDTO1 =
        sut_dropdownOptionService.createDropdownOptionDTO(dropdownOption1);

    DropdownOption dropdownOption2 = dropdownOptionUtils.createDropdownOption();
    dropdownOption2.setName("test_name_2");
    dropdownOption2.setGetterDropdownOptionCallAddress("https://my-address:1234/my-endpoint");
    dropdownOption2.setType(DropdownOption.TypeEnum.DYNAMIC);
    DropdownOptionDTO dropdownOptionDTO2 =
        sut_dropdownOptionService.createDropdownOptionDTO(dropdownOption2);

    return new ArrayList<DropdownOptionDTO>() {
      {
        add(dropdownOptionDTO1);
        add(dropdownOptionDTO2);
      }
    };
  }
}
