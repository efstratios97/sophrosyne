package sophrosyne.core.controlpanelservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sophrosyne.core.controlpanelservice.dto.ControlPanelDashboardGroupDTO;

@Repository
@Transactional
public interface ControlPanelDashboardGroupRepository
    extends JpaRepository<ControlPanelDashboardGroupDTO, String> {}
