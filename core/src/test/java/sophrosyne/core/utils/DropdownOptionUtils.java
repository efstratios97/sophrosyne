package sophrosyne.core.utils;

import java.util.ArrayList;
import org.springframework.stereotype.Service;
import sophrosyne_api.core.internaldropdownoptionservice.model.DropdownOption;

@Service
public class DropdownOptionUtils {

  public DropdownOption createDropdownOption() {
    DropdownOption dropdownOption = new DropdownOption();
    dropdownOption.setDescription("test_desc");
    dropdownOption.setName("test_dropdownOption");
    dropdownOption.setType(DropdownOption.TypeEnum.STATIC);
    dropdownOption.setDropdownOptions(

            new ArrayList<String>() {
              {
                add("A");
                add("B");
                add("C");
              }
            });
    return dropdownOption;
  }
}
