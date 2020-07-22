package repository.status;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import repository.event.Event;

@Entity
@Table(name = "EventStatus")
public class EventStatus {
	
	@EmbeddedId
	EventStatusKey id = new EventStatusKey();
	
	@ManyToOne
	@MapsId("eventId")
	@JoinColumn(name="EventID")
	public Event event;
	
	@ManyToOne
	@MapsId("statusId")
	@JoinColumn(name="statusId")
	public Status status;
	
	public EventStatus() {}

	public EventStatusKey getId() {
		return id;
	}

	public void setId(EventStatusKey id) {
		this.id = id;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	};
	
	

}
