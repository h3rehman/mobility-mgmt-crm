package repository.event.audience;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AudiencetypeRepository extends JpaRepository<Audiencetype, Long> {

}
