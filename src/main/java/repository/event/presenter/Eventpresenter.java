package repository.event.presenter;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import repository.event.Event;

@Entity
@Table(name = "Eventpresenter")
public class Eventpresenter {

	@EmbeddedId
	EventPresenterKey id;
	
	@ManyToOne
	@MapsId("eventId")
	@JoinColumn(name = "EventID")
	public Event event;

	@ManyToOne
	@MapsId("presenterId")
	@JoinColumn(name = "PresenterID")
	public Presenter presenter;

	public EventPresenterKey getId() {
		return id;
	}
		
}
