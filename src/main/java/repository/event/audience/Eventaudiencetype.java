package repository.event.audience;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import repository.event.Event;

@Entity
@Table(name = "Eventaudiencetype")
public class Eventaudiencetype {
	
	@EmbeddedId
	EventAudienceTypeKey id;
	
	@ManyToOne
	@MapsId("eventId")
	@JoinColumn(name = "EventID")
	Event event;
	
	@ManyToOne
	@MapsId("audiencetypeId")
	@JoinColumn(name = "audiencetypeid")
	Audiencetype audienceType;

	public EventAudienceTypeKey getId() {
		return id;
	}

	public void setId(EventAudienceTypeKey id) {
		this.id = id;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}

	public Audiencetype getAudienceType() {
		return audienceType;
	}

	public void setAudienceType(Audiencetype audienceType) {
		this.audienceType = audienceType;
	}

}
