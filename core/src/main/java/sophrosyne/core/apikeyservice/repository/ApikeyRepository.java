package sophrosyne.core.apikeyservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import sophrosyne.core.apikeyservice.dto.ApikeyDTO;

@Transactional
public interface ApikeyRepository extends JpaRepository<ApikeyDTO, String> {

  Optional<ApikeyDTO> findByApikey(String apikey);

  Optional<ApikeyDTO> findByApikeyname(String apikeyname);
}
