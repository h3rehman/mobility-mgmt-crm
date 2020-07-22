package repository.event;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "Eventtype")
public class Eventtype {
	
	@Id
	@Column(name = "EventtypeID")
	long eventTypeId;
	
	@Column(name = "eventtypedesc")
	String eventTypeDesc;
	
	@OneToMany(mappedBy = "eventType")
	private List<Event> events = new ArrayList<Event>();

	public long getEventTypeId() {
		return eventTypeId;
	}

	public void setEventTypeId(long eventTypeId) {
		this.eventTypeId = eventTypeId;
	}

	public String getEventTypeDesc() {
		return eventTypeDesc;
	}

	public void setEventTypeDesc(String eventTypeDesc) {
		this.eventTypeDesc = eventTypeDesc;
	}

}
