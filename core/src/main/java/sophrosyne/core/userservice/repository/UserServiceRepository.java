package sophrosyne.core.userservice.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import sophrosyne.core.userservice.dto.UserDTO;

@Repository
@Transactional
public interface UserServiceRepository extends JpaRepository<UserDTO, String> {

  Optional<UserDTO> findByUsername(String username);

  void deleteByUsername(String username);
}
