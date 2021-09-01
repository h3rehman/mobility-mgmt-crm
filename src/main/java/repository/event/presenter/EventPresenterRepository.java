package repository.event.presenter;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import repository.event.Event;
import repository.event.Eventtype;
import repository.status.Status;

/**
 * 
 * @author Hamood
 * Most of the methods have nested objects arising from the Event class.  
 *
 */

@Repository
public interface EventPresenterRepository extends JpaRepository<Eventpresenter, Long> {
	
	public Eventpresenter findByEventAndPresenter(Event event, Presenter presenter);

	//Default query
	public Page<Eventpresenter> findByPresenterAndEventStartDateTimeGreaterThanEqual(Presenter user, LocalDateTime dateTime, Pageable pageable);
	
	public Page<Eventpresenter> findByPresenterAndEventEventTypeInAndEventStartDateTimeGreaterThanEqual(Presenter user, List<Eventtype> eventTypes, LocalDateTime dateTime, Pageable pageable);
		
	public Page<Eventpresenter> findByPresenterAndEventEventTypeInAndEventLastStatusInAndEventStartDateTimeGreaterThanEqual(Presenter user, List<Eventtype> eventTypes, List<Status> lastStatuses, LocalDateTime dateTime, Pageable pageable);
		
	public Page<Eventpresenter> findByPresenterAndEventLastStatusInAndEventStartDateTimeGreaterThanEqual(Presenter user, List<Status> lastStatuses, LocalDateTime dateTime, Pageable pageable);
			
	public Page<Eventpresenter> findByPresenterAndEventStartDateTimeBetween(Presenter user, LocalDateTime fromDate, LocalDateTime toDate, Pageable pageable);

	public Page<Eventpresenter> findByPresenterAndEventStartDateTimeBetweenAndEventEventTypeIn(Presenter user, LocalDateTime fromDate, LocalDateTime toDate, List<Eventtype> eventTypes, Pageable pageable);
		
	public Page<Eventpresenter> findByPresenterAndEventStartDateTimeBetweenAndEventLastStatusIn (Presenter user, LocalDateTime fromDate, LocalDateTime toDate, List<Status> lastStatuses, Pageable pageable);
		
	public Page<Eventpresenter> findByPresenterAndEventStartDateTimeBetweenAndEventEventTypeInAndEventLastStatusIn(Presenter user, LocalDateTime fromDate, LocalDateTime toDate, List<Eventtype> eventTypes, List<Status> lastStatuses, Pageable pageable);
		
	public Page<Eventpresenter> findByPresenterAndEventEventTypeIn(Presenter user, List<Eventtype> eventTypes, Pageable pageable);
		
	public Page<Eventpresenter> findByPresenterAndEventEventTypeInAndEventLastStatusIn(Presenter user, List<Eventtype> eventTypes, List<Status> lastStatuses, Pageable pageable);
	
	public Page<Eventpresenter> findByPresenterAndEventLastStatusIn(Presenter user, List<Status> lastStatuses, Pageable pageable);
	
	public Page<Eventpresenter> findByPresenter(Presenter user, Pageable pageable);
	
	public List<Eventpresenter> findByPresenter(Presenter user);

}
