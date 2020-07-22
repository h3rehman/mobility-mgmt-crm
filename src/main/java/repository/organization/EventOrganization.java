package repository.organization;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import repository.event.Event;

@Entity
@Table(name = "eventorganization")
public class EventOrganization {

	@EmbeddedId
	EventOrganizationKey id = new EventOrganizationKey();
	
	@ManyToOne
	@MapsId("orgId")
	@JoinColumn(name="OrgID")
	public Organization organization;
	
	@ManyToOne
	@MapsId("eventId")
	@JoinColumn(name="EventID")
	public Event event;
	
	public EventOrganization() {}

	public EventOrganizationKey getId() {
		return id;
	}

	public void setId(EventOrganizationKey id) {
		this.id = id;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public Event getEvent() {
		return event;
	}

	public void setEvent(Event event) {
		this.event = event;
	}
	
}
