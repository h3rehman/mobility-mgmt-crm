package repository.organization.contact;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Composite Key for Customer and Contact Tables
 */
@Embeddable
public class OrganizationContactKey implements Serializable{
	
	private static final long serialVersionUID = 1L;

	@Column(name="orgid")
	private Long orgId;
	
	@Column(name="contactid")
	private Long contactId;
	
	//Default Constructor for JPA
	OrganizationContactKey(){}
	
	OrganizationContactKey(Long customerId, Long contactId){
		this.orgId = customerId;
		this.contactId = contactId;
	}
	
	public Long getOrgId() {
		return orgId;
	}
	
	public Long getContactId() {
		return contactId;
	}
	
	public void setOrgId(Long customerId) {
		this.orgId = customerId;
	}
	
	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (contactId ^ (contactId >>> 32));
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
		OrganizationContactKey other = (OrganizationContactKey) obj;
		if (contactId != other.contactId)
			return false;
		if (orgId != other.orgId)
			return false;
		return true;
	}
	
}