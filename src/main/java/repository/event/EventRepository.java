package repository.event;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>{

	@Query("SELECT e from Event e WHERE e.startDateTime >= ?1")
	List<Event> findByDate(LocalDateTime dateTime);
	
}
 