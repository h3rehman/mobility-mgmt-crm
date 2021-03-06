package repository.organization.contact;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

import repository.event.presenter.Presenter;
import repository.organization.View;

@Entity
@Table(name="Contact")
public class Contact {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="ContactID")
	@JsonView(View.OrgDetail.class)
	Long contactId;
	
	@JsonView(View.OrgDetail.class)
	@Column(name="firstname")
	String firstName;
	
	@JsonView(View.OrgDetail.class)
	@Column(name="lastname")
	String lastName;
	@JsonView(View.OrgDetail.class)
	String title;
	@JsonView(View.OrgDetail.class)
	String email;
	@JsonView(View.OrgDetail.class)
	String phone;
	@JsonView(View.OrgDetail.class)
	@Column(name="altphone")
	String altPhone;
	
	@Column(name = "createddate", columnDefinition = "TIMESTAMP")
	private LocalDateTime createDate;
	
	@Column(name = "lastmodifieddate", columnDefinition = "TIMESTAMP")
	private LocalDateTime lastModifiedDate;
	
	@ManyToOne
	@JoinColumn(name = "createdby")
	Presenter createdBy;
	
	@ManyToOne
	@JoinColumn(name = "lastmodifiedby")
	Presenter lastModifiedBy;

	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="ContactID")
	List<OrganizationContact> contactOrgs;
	
	//Empty contructor for JPA
	public Contact () {}

	public String getFirstName() {
		return firstName;
	}
	
	public String getLastName() {
		return lastName;
	}
	
	public String getTitle() {
		return title;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getPhone() {
		return phone;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public Long getContactId() {
		return contactId;
	}

	public void setContactId(Long contactId) {
		this.contactId = contactId;
	}


	public HashMap<Long, String> getContactOrgs() {
		HashMap<Long, String> orgs = new HashMap<Long, String>();
		for (OrganizationContact contactOrg : contactOrgs) {
			orgs.put(contactOrg.getOrganization().getOrgId(), contactOrg.getOrganization().getOrgname());
		}
		return orgs;
	}


	public void setContactOrgs(List<OrganizationContact> customerContacts) {
		this.contactOrgs = customerContacts;
	}

	public String getAltPhone() {
		return altPhone;
	}

	public void setAltPhone(String altPhone) {
		this.altPhone = altPhone;
	}

	public LocalDateTime getCreateDate() {
		return createDate;
	}

	public void setCreateDate(LocalDateTime createDate) {
		this.createDate = createDate;
	}

	public LocalDateTime getLastModifiedDate() {
		return lastModifiedDate;
	}

	public void setLastModifiedDate(LocalDateTime lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}

	public String getCreatedBy() {
		if (createdBy != null) {
			return createdBy.getName() + " " + createdBy.getLastName();
		}
		return null;
	}

	public void setCreatedBy(Presenter createdBy) {
		this.createdBy = createdBy;
	}

	public String getLastModifiedBy() {
		if (lastModifiedBy != null) {
			return lastModifiedBy.getName() + " " + lastModifiedBy.getLastName();
		}
		return null;
	}

	public void setLastModifiedBy(Presenter lastModifiedBy) {
		this.lastModifiedBy = lastModifiedBy;
	}
		
}
