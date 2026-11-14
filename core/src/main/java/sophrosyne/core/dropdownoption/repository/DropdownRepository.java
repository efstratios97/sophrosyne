package sophrosyne.core.dropdownoption.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sophrosyne.core.dropdownoption.dto.DropdownOptionDTO;

@Repository
@Transactional
public interface DropdownRepository extends JpaRepository<DropdownOptionDTO, String> {}
