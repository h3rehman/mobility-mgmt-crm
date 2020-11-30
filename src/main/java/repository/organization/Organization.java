package repository.organization;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import javax.validation.constraints.NotNull;

import org.springframework.context.annotation.Lazy;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonView;

import repository.event.Event;
import repository.organization.contact.Contact;
import repository.organization.contact.OrganizationContact;
import repository.organization.county.County;

@Entity
@Table(name="Organization")
public class Organization {
	
	@JsonView(View.OrgDetail.class)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="OrgID")
	Long orgId;
	
	@JsonView(View.OrgDetail.class)
	String orgname;
	@JsonView(View.OrgDetail.class)
	String address;
	@JsonView(View.OrgDetail.class)
	String city;
	@JsonView(View.OrgDetail.class)
	String email;
	@JsonView(View.OrgDetail.class)
	String phone;
	String altphone;
	@JsonView(View.OrgDetail.class)
	String zip;
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name="OrgID")
	@JsonIgnore
//	@NotNull
	Set<OrganizationContact> orgContacts;
	
	@ManyToOne(cascade=CascadeType.ALL)
	@JoinColumn(name = "CountyID")
	@JsonIgnore
	County county; 
	
	@OneToMany(cascade=CascadeType.ALL)
	@JoinColumn(name = "OrgID")
	@JsonIgnore
	Set<EventOrganization> eventOrgs;
	
	Organization (String orgName, String email){
		this.orgname = orgName;
		this.email = email;
	}
	//default constructor for JPA
	Organization (){}

	//getters and setters
	public Long getOrgId() {
		return orgId;
	}
	
	public String getEmail() {
		return email;
	}
	
	public String getPhone() {
		return phone;
	}
		
	public void setOrgname(String orgname) {
		this.orgname = orgname;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public void setAltphone(String altphone) {
		this.altphone = altphone;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public void setOrgContacts(Set<OrganizationContact> orgContacts) {
		this.orgContacts = orgContacts;
	}
	
	@JsonView(View.OrgDetail.class)
	public List<Contact> getOrgContacts() {
		List<Contact> contacts = new ArrayList<>();
		for (OrganizationContact co : orgContacts) {
			contacts.add(co.getContact());
		}
		return contacts;
	}
	
	public String getOrgname() {
		return orgname;
	}
	public String getAddress() {
		return address;
	}
	public String getCity() {
		return city;
	}
	public String getAltphone() {
		return altphone;
	}
	public String getZip() {
		return zip;
	}
	@JsonView(View.OrgDetail.class)
	public String getCountyName() {
		return county.getCountyDesc();
	}
		
	public County getCounty() {
		return county;
	}

	public void setCounty(County county) {
		this.county = county;
	}
	
	@JsonView(View.OrgDetail.class)
	public List<Event> getEventOrgs() {
		
		List<Event> events = new ArrayList<>();

		for (EventOrganization evOrg : eventOrgs) {
			events.add(evOrg.getEvent());
		}
		return events;
	}
		
	@JsonIgnore
	public void setEventOrgs(Set<EventOrganization> eventOrgs) {
		this.eventOrgs = eventOrgs;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
}
