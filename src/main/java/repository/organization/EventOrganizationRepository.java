package repository.organization;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventOrganizationRepository extends JpaRepository<EventOrganization, Long> {

}
