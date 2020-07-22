package repository.organization.contact;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import repository.organization.Organization;

/**
 * @author RehmanH
 * Join Table for Customer and Contact. No PK only FKs. 
 *
 */

@Entity
@Table(name="organizationcontact")
public class OrganizationContact {
	
	@EmbeddedId
	OrganizationContactKey id= new OrganizationContactKey();
	
	@ManyToOne//(targetEntity=Organization.class)
	@MapsId("orgId")
	@JoinColumn(name="orgid")
//	@JsonIgnore
	public Organization organization;
	
	@ManyToOne//(targetEntity=Contact.class)
	@MapsId("contactId")
	@JoinColumn(name="contactid")
//	@JsonIgnore
	public Contact contact;
		
	public OrganizationContact(){}
	
	public OrganizationContactKey getCustomerContactKey() {
		return id;
	}
	
	public void setCustomerContactKey(Long customerId, Long contactId) {
		id = new OrganizationContactKey(customerId, contactId); 
	}

	public Contact getContact() {
		return contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	public Organization getOrganization() {
		return organization;
	}

	public void setOrganization(Organization organization) {
		this.organization = organization;
	}

	public OrganizationContactKey getId() {
		return id;
	}

	public void setId(OrganizationContactKey id) {
		this.id = id;
	}
	
	
}
