package repository.organization;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class EventOrganizationKey implements Serializable{

	private static final long serialVersionUID = 1L;
	
	@Column(name = "EventID")
	private long eventId;
	
	@Column(name = "OrgID")
	private long orgId;
	
	EventOrganizationKey(){}

	public long getEventId() {
		return eventId;
	}

	public void setEventId(long eventId) {
		this.eventId = eventId;
	}

	public long getOrgId() {
		return orgId;
	}

	public void setOrgId(long orgId) {
		this.orgId = orgId;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (eventId ^ (eventId >>> 32));
		result = prime * result + (int) (orgId ^ (orgId >>> 32));
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
		EventOrganizationKey other = (EventOrganizationKey) obj;
		if (eventId != other.eventId)
			return false;
		if (orgId != other.orgId)
			return false;
		return true;
	}

}
