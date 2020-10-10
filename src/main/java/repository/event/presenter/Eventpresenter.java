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
	EventPresenterKey id = new EventPresenterKey();
	
	@ManyToOne
	@MapsId("eventId")
	@JoinColumn(name = "EventID")
	public Event event;

	@ManyToOne
	@MapsId("presenterId")
	@JoinColumn(name = "PresenterID")
	public Presenter presenter;

	public Eventpresenter(){}
	
	public EventPresenterKey getId() {
		return id;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public Presenter getPresenter() {
		return presenter;
	}

	public void setPresenter(Presenter presenter) {
		this.presenter = presenter;
	}

	public void setId(EventPresenterKey id) {
		this.id = id;
	}
		
}
