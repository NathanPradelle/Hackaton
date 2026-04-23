package esgi.hackathon.wsd.repository;

import esgi.hackathon.wsd.entity.users.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
}
