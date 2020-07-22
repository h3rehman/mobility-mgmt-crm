package repository.organization.county;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CountyRepository extends JpaRepository<County, Long>{

	public County findBycountyDesc(String countyName);
}
