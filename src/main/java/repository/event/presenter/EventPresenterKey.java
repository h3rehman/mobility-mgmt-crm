package repository.event.presenter;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class EventPresenterKey implements Serializable{

	private static final long serialVersionUID = 1L;

	@Column(name = "EventID")
	private long eventId;
	
	@Column(name = "PresenterID")
	private long presenterId;

	public long getEventId() {
		return eventId;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

	public long getPresenterId() {
		return presenterId;
	}

	public void setPresenterId(long presenterId) {
		this.presenterId = presenterId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (eventId ^ (eventId >>> 32));
		result = prime * result + (int) (presenterId ^ (presenterId >>> 32));
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
		EventPresenterKey other = (EventPresenterKey) obj;
		if (eventId != other.eventId)
			return false;
		if (presenterId != other.presenterId)
			return false;
		return true;
	}
	
}
