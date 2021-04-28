package repository.organization.contact;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long>{

	public Contact findByfirstName(String firstName);
	
	public Page<Contact> findByLastContactDateBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);
}
