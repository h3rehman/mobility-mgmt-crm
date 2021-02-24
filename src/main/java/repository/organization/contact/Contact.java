package repository.organization.contact;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

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

	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="ContactID")
	@JsonIgnore
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

	public List<OrganizationContact> getContactOrgs() {
		return contactOrgs;
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
		
}
