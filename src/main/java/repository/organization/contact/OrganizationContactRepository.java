package repository.organization.contact;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import repository.organization.Organization;

@Repository
public interface OrganizationContactRepository extends JpaRepository<OrganizationContact, Long>{

	
		@Query("SELECT oc.contact "
		+ "FROM OrganizationContact oc "
		+ "WHERE (oc.organization = :org)")
		List<Contact> findByOrganization (@Param("org") Organization org);
}
