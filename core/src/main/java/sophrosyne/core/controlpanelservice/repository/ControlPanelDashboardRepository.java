package sophrosyne.core.controlpanelservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sophrosyne.core.controlpanelservice.dto.ControlPanelDashboardDTO;

@Repository
@Transactional
public interface ControlPanelDashboardRepository
    extends JpaRepository<ControlPanelDashboardDTO, String> {}
