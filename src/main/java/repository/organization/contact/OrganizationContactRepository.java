package repository.organization.contact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrganizationContactRepository extends JpaRepository<OrganizationContact, Long>{

}
