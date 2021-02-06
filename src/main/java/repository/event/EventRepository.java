package repository.event;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>{

	@Query("SELECT e from Event e WHERE e.startDateTime >= ?1")
	List<Event> findByDate(LocalDateTime dateTime);
	
	Page<Event> findAllByeventType(Eventtype eventType, Pageable pageable);
	
	@Query("SELECT e from Event e WHERE e.startDateTime between ?1 and ?2")
	Page<Event> findBystartDateTimeBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);

	@Query("SELECT e from Event e WHERE e.startDateTime between ?1 and ?2 AND e.eventType = ?3")
	Page<Event> findBystartDateTimeBetweenAndeventType(LocalDateTime from, LocalDateTime to, 
			Eventtype eventType, Pageable pageable);
	
	@Query("SELECT e from Event e WHERE e.startDateTime between ?1 and ?2 AND e.eventType IN ?3")
	Page<Event> findBystartDateTimeBetweenAndeventTypeIn(LocalDateTime from, LocalDateTime to, 
			List<Eventtype> eventTypes, Pageable pageable);
	
	Page<Event> findAllByeventTypeIn(List<Eventtype> eventTypes, Pageable pageable);
	
	Page<Event> findByeventTypeIn(List<Eventtype> eventTypes, Pageable pageable);

}
 