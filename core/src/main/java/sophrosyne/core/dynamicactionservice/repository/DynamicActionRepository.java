package sophrosyne.core.dynamicactionservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sophrosyne.core.dynamicactionservice.dto.DynamicActionDTO;

@Repository
@Transactional
public interface DynamicActionRepository extends JpaRepository<DynamicActionDTO, String> {}
