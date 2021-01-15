package repository.event.audience;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class EventAudienceTypeKey implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Column(name = "EventID")
	private Long eventId;
	
	@Column(name = "audiencetypeid")
	private Long audiencetypeId;

	public Long getEventId() {
		return eventId;
	}

	public void setEventId(Long eventId) {
		this.eventId = eventId;
	}

	public Long getAudiencetypeId() {
		return audiencetypeId;
	}

	public void setAudiencetypeId(Long audiencetypeId) {
		this.audiencetypeId = audiencetypeId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (audiencetypeId ^ (audiencetypeId >>> 32));
		result = prime * result + (int) (eventId ^ (eventId >>> 32));
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
		EventAudienceTypeKey other = (EventAudienceTypeKey) obj;
		if (audiencetypeId != other.audiencetypeId)
			return false;
		if (eventId != other.eventId)
			return false;
		return true;
	}
	
	

}
