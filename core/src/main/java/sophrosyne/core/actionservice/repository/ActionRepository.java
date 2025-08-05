package sophrosyne.core.actionservice.repository;

import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import sophrosyne.core.actionservice.dto.ActionDTO;

@Repository
@Transactional
public interface ActionRepository extends JpaRepository<ActionDTO, String> {

  @Query(
      "SELECT a FROM sophrosyne_action a WHERE TYPE(a) = sophrosyne.core.actionservice.dto.ActionDTO")
  List<ActionDTO> findAll();
}
