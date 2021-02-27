package repository.event;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Repository;

import repository.status.Status;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>{

	@Query("SELECT e from Event e WHERE e.startDateTime >= ?1")
	List<Event> findByDate(LocalDateTime dateTime);

	//@Query("SELECT e from Event e WHERE e.startDateTime between ?1 and ?2")
	Page<Event> findByStartDateTimeBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);
	
	Page<Event> findAllByeventTypeIn(List<Eventtype> eventTypes, Pageable pageable);
	
	Page<Event> findByEventTypeIn(List<Eventtype> eventTypes, Pageable pageable);
	
	Page<Event> findByEventTypeInAndLastStatusIsNull(List<Eventtype> eventTypes, Pageable pageable);
	
	Page<Event> findByEventTypeInAndLastStatusIn(List<Eventtype> eventTypes, 
			List<Status> lastStauses, Pageable pageable);
	
	Page<Event> findByLastStatusIn(List<Status> eventStatuses, Pageable pageable);

	@Query("SELECT e from Event e WHERE e.lastStatus is null")
	Page<Event> findBylastStatusNull(Pageable pageable);
	
//	@Query("SELECT e from Event e WHERE e.startDateTime between ?1 and ?2 AND e.eventType IN ?3")
	Page<Event> findByStartDateTimeBetweenAndEventTypeIn(LocalDateTime from, LocalDateTime to, 
			List<Eventtype> eventTypes, Pageable pageable);
	
	Page<Event> findByStartDateTimeBetweenAndEventTypeInAndLastStatusIn(LocalDateTime from, LocalDateTime to, 
			List<Eventtype> eventTypes, List<Status> lastStatuses, Pageable pageable);
		
	@Query("SELECT e from Event e WHERE e.startDateTime between ?1 and ?2 "
		 + "AND e.eventType IN ?3 AND e.lastStatus = ?4")
	Page<Event> findBystartDateTimeBetweenAndeventTypeInAndlastStatus(LocalDateTime from, LocalDateTime to, 
			List<Eventtype> eventTypes, Status eventStatus, Pageable pageable);
	
	Page<Event> findByStartDateTimeBetweenAndEventTypeInAndLastStatusIsNull
	(LocalDateTime from, LocalDateTime to, List<Eventtype> eventTypes, Pageable pageable);
	
	@Query("SELECT e from Event e WHERE e.startDateTime between ?1 and ?2 "
			 + "AND e.eventType IN ?3 AND e.lastStatus is null")
	Page<Event> findBystartDateTimeBetweenAndeventTypeInAndlastStatusNull
	(LocalDateTime from, LocalDateTime to, List<Eventtype> eventTypes, Pageable pageable);
	
	@Query("SELECT e from Event e WHERE e.startDateTime between ?1 and ?2 "
			 + "AND e.lastStatus is null")
	Page<Event> findBystartDateTimeBetweenAndlastStatusNull
	(LocalDateTime from, LocalDateTime to, Pageable pageable);
	
	Page<Event> findByStartDateTimeBetweenAndLastStatusIn
	(LocalDateTime from, LocalDateTime to, List<Status> lastStauses, Pageable pageable);
	
}
 