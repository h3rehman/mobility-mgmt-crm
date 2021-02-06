package repository.event;


import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@Entity
@Table(name = "Eventtype")
@JsonInclude(Include.NON_NULL)
public class Eventtype {
	
	@Id
	@Column(name = "EventtypeID")
	Long eventTypeId;
	
	@Column(name = "eventtypedesc")
	String eventTypeDesc;
	
	@OneToMany(mappedBy = "eventType")
	private Set<Event> events;

	public Long getEventTypeId() {
		return eventTypeId;
	}

	public void setEventTypeId(Long eventTypeId) {
		this.eventTypeId = eventTypeId;
	}

	public String getEventTypeDesc() {
		return eventTypeDesc;
	}

	public void setEventTypeDesc(String eventTypeDesc) {
		this.eventTypeDesc = eventTypeDesc;
	}

	public void setEvents(Set<Event> events) {
		this.events = events;
	}
	
	@JsonIgnore
	public Set<Event> getEvents() {
		return events;
	}

}
