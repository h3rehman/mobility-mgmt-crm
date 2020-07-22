package repository.status;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class EventStatusKey implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Column(name = "EventID")
	private long eventId;
	
	@Column(name = "statusId")
	private long statusId;
	
	EventStatusKey(){}

	public long getEventId() {
		return eventId;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

	public long getStatusId() {
		return statusId;
	}

	public void setStatusId(long statusId) {
		this.statusId = statusId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (eventId ^ (eventId >>> 32));
		result = prime * result + (int) (statusId ^ (statusId >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EventStatusKey other = (EventStatusKey) obj;
		if (eventId != other.eventId)
			return false;
		if (statusId != other.statusId)
			return false;
		return true;
	}
	
	

}
