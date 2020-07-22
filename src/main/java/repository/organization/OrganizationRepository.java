package repository.organization;

import java.util.List;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long>{
	
//	Optional<Customer> findByName(String orgName);
	@Query("SELECT c.orgname from Organization c")
	List<String> findAllNames();
	
	public Organization findByorgname (String name);
	
	@Query("SELECT o from Organization o "
		+ "JOIN o.county c "
		+ "WHERE (c.countyDesc = :county or :county is null or :county = '')"
		+ "AND (o.orgname = :name or :name is null or :name = '')")
	List<Organization> findBycountyAndorgnameAllIgnoreCase (@Param("county") String county, @Param("name") String orgName);

}
