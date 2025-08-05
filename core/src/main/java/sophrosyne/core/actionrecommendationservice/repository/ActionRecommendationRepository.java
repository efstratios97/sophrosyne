package sophrosyne.core.actionrecommendationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sophrosyne.core.actionrecommendationservice.dto.ActionRecommendationDTO;

@Repository
@Transactional
public interface ActionRecommendationRepository
    extends JpaRepository<ActionRecommendationDTO, String> {}
