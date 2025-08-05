package sophrosyne.core.actionarchiveservice.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sophrosyne.core.actionarchiveservice.dto.ActionArchiveDTO;

@Repository
@Transactional
public interface ActionArchiveRepository extends JpaRepository<ActionArchiveDTO, String> {

  Optional<ActionArchiveDTO> findById(Long id);

  List<ActionArchiveDTO> findAllByActionId(String actionId);

  Optional<ActionArchiveDTO> findTopByActionIdOrderByExecutionStartPointDesc(String actionId);

  List<ActionArchiveMetadata> findAllBy();
}
