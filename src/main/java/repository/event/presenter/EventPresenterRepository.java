package repository.event.presenter;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import repository.event.Event;

@Repository
public interface EventPresenterRepository extends JpaRepository<Eventpresenter, Long> {
	
	public Eventpresenter findByEventAndPresenter(Event event, Presenter presenter);

}
