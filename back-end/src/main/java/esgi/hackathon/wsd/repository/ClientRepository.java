package esgi.hackathon.wsd.repository;

import esgi.hackathon.wsd.entity.users.Client;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<Client> findByUserId(Long userId);
}
